import NativeModule from "../native/FejaModule";
import type { BeaconTargetInput } from "../beacon/target";
import { BeaconCommandError, BeaconResponseError } from "./command-errors";
import { Match, Result } from "effect";
import { decodeNativeCommandResult, type NativeCommandResult } from "../native/command-result";
import { makeNativePayloadIssues } from "../native/payload-issues";

const interpretCommandResult = Match.type<NativeCommandResult>().pipe(
  Match.discriminatorsExhaustive("status")({
    success: () => Result.succeed(undefined),
    failure: ({ error }) => Result.fail(new BeaconCommandError(error)),
  }),
);

const completeCommand = (input: unknown) =>
  decodeNativeCommandResult(input).pipe(
    Result.mapError((error) => new BeaconResponseError(makeNativePayloadIssues(error))),
    Result.flatMap(interpretCommandResult),
    Result.match({
      onSuccess: () => {},
      onFailure: (error) => {
        throw error;
      },
    }),
  );

export const startRanging = async (target: BeaconTargetInput) => {
  const response = await NativeModule.start(target);
  completeCommand(response);
};

export const stopRanging = async (target: BeaconTargetInput) => {
  const response = await NativeModule.stop(target);
  completeCommand(response);
};
