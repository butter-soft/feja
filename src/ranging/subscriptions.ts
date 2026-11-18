import { Result } from "effect";
import type { EventSubscription } from "expo";

import {
  decodeBeaconRangingStartedEvent,
  decodeBeaconRangingReceivedEvent,
  decodeBeaconRangingTerminatedEvent,
  decodeBeaconRangingMeasurementParseErrorEvent,
} from "./event";
import type {
  BeaconRangingStartedEvent,
  BeaconRangingReceivedEvent,
  BeaconRangingTerminatedEvent,
  BeaconRangingMeasurementParseErrorEvent,
} from "./event";

import NativeModule from "../native/FejaModule";
import { makeNativePayloadIssues, type NativePayloadIssue } from "../native/payload-issues";

export const subscribeRangingStarted = (
  onStarted: (event: BeaconRangingStartedEvent) => void,
  onDecodeError: (issues: readonly NativePayloadIssue[]) => void,
): EventSubscription =>
  NativeModule.addListener("onRangingStarted", (payload) => {
    decodeBeaconRangingStartedEvent(payload).pipe(
      Result.mapError(makeNativePayloadIssues),
      Result.match({
        onSuccess: onStarted,
        onFailure: onDecodeError,
      }),
    );
  });

export const subscribeRangingReceived = (
  onReceived: (event: BeaconRangingReceivedEvent) => void,
  onDecodeError: (issues: readonly NativePayloadIssue[]) => void,
): EventSubscription =>
  NativeModule.addListener("onRangingReceived", (payload) => {
    decodeBeaconRangingReceivedEvent(payload).pipe(
      Result.mapError(makeNativePayloadIssues),
      Result.match({
        onSuccess: onReceived,
        onFailure: onDecodeError,
      }),
    );
  });

export const subscribeRangingTerminated = (
  onTerminated: (event: BeaconRangingTerminatedEvent) => void,
  onDecodeError: (issues: readonly NativePayloadIssue[]) => void,
): EventSubscription =>
  NativeModule.addListener("onRangingTerminated", (payload) =>
    decodeBeaconRangingTerminatedEvent(payload).pipe(
      Result.mapError(makeNativePayloadIssues),
      Result.match({
        onSuccess: onTerminated,
        onFailure: onDecodeError,
      }),
    ),
  );

export const subscribeRangingMeasurementParseError = (
  onParseError: (event: BeaconRangingMeasurementParseErrorEvent) => void,
  onDecodeError: (issues: readonly NativePayloadIssue[]) => void,
): EventSubscription =>
  NativeModule.addListener("onRangingMeasurementParseError", (payload) =>
    decodeBeaconRangingMeasurementParseErrorEvent(payload).pipe(
      Result.mapError(makeNativePayloadIssues),
      Result.match({
        onSuccess: onParseError,
        onFailure: onDecodeError,
      }),
    ),
  );
