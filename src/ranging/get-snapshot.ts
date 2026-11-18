import { Result } from "effect";

import NativeModule from "../native/FejaModule";
import { makeNativePayloadIssues } from "../native/payload-issues";
import { BeaconResponseError } from "./command-errors";
import { decodeBeaconRangingSnapshot } from "./execution/snapshot";

export const getRangingSnapshot = async () => {
  const response = await NativeModule.getSnapshot();

  return decodeBeaconRangingSnapshot(response).pipe(
    Result.mapError(makeNativePayloadIssues),
    Result.match({
      onSuccess: (snapshot) => snapshot,
      onFailure: (issues) => {
        throw new BeaconResponseError(issues);
      },
    }),
  );
};
