import { Effect } from "effect";
import type { EventSubscription } from "expo";
import type {
  BeaconRangingStartedEvent,
  BeaconRangingReceivedEvent,
  BeaconRangingTerminatedEvent,
  BeaconRangingMeasurementParseErrorEvent,
} from "./event";
import type { BeaconRangingSnapshot } from "./execution/snapshot";
import { getRangingSnapshot } from "./get-snapshot";
import {
  subscribeRangingStarted,
  subscribeRangingReceived,
  subscribeRangingTerminated,
  subscribeRangingMeasurementParseError,
} from "./subscriptions";
import type { NativePayloadIssue } from "../native/payload-issues";

type RangingObserver = {
  onSnapshot: (snapshot: BeaconRangingSnapshot) => void;
  onStarted: (event: BeaconRangingStartedEvent) => void;
  onReceived: (event: BeaconRangingReceivedEvent) => void;
  onTerminated: (event: BeaconRangingTerminatedEvent) => void;
  onMeasurementParseError: (event: BeaconRangingMeasurementParseErrorEvent) => void;
  onDecodeError: (
    event: "started" | "received" | "terminated" | "measurementParseError",
    issues: readonly NativePayloadIssue[],
  ) => void;
  onSnapshotError: (error: unknown) => void;
};

const acquireSubscription = (subscribe: () => EventSubscription) =>
  Effect.acquireRelease(Effect.sync(subscribe), (subscription) =>
    Effect.sync(() => {
      subscription.remove();
    }),
  );

const observeStarted = (observer: RangingObserver) =>
  acquireSubscription(() =>
    subscribeRangingStarted(
      (event) => observer.onStarted(event),
      (issues) => observer.onDecodeError("started", issues),
    ),
  );

const observeReceived = (observer: RangingObserver) =>
  acquireSubscription(() =>
    subscribeRangingReceived(
      (event) => observer.onReceived(event),
      (issues) => observer.onDecodeError("received", issues),
    ),
  );

const observeTerminated = (observer: RangingObserver) =>
  acquireSubscription(() =>
    subscribeRangingTerminated(
      (event) => observer.onTerminated(event),
      (issues) => observer.onDecodeError("terminated", issues),
    ),
  );

const observeMeasurementParseError = (observer: RangingObserver) =>
  acquireSubscription(() =>
    subscribeRangingMeasurementParseError(
      (event) => observer.onMeasurementParseError(event),
      (issues) => observer.onDecodeError("measurementParseError", issues),
    ),
  );

const loadSnapshot = (observer: RangingObserver) =>
  Effect.tryPromise({
    try: () => getRangingSnapshot(),
    catch: (error) => error,
  }).pipe(
    Effect.tap((snapshot) =>
      Effect.sync(() => {
        observer.onSnapshot(snapshot);
      }),
    ),
    Effect.catch((error) =>
      Effect.sync(() => {
        observer.onSnapshotError(error);
      }),
    ),
  );

export const observeRanging = (observer: RangingObserver) =>
  Effect.gen(function* () {
    yield* observeStarted(observer);
    yield* observeReceived(observer);
    yield* observeTerminated(observer);
    yield* observeMeasurementParseError(observer);

    yield* loadSnapshot(observer);

    yield* Effect.never;
  }).pipe(Effect.scoped);
