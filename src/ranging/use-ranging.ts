import { useEffect, useEffectEvent, useState } from "react";

import { startRanging, stopRanging } from "./commands";
import type {
  BeaconRangingStartedEvent,
  BeaconRangingReceivedEvent,
  BeaconRangingTerminatedEvent,
  BeaconRangingMeasurementParseErrorEvent,
} from "./event";
import { snapshotFromRangingStarted, snapshotFromRangingTerminated } from "./event/snapshot";
import { selectRangingSnapshot, type BeaconRangingSnapshot } from "./execution/snapshot";
import { Effect } from "effect";
import { observeRanging } from "./observation";

export type UseRangingOptions = {
  onStarted?: (event: BeaconRangingStartedEvent) => void;
  onReceived?: (event: BeaconRangingReceivedEvent) => void;
  onTerminated?: (event: BeaconRangingTerminatedEvent) => void;
  onMeasurementParseError?: (event: BeaconRangingMeasurementParseErrorEvent) => void;
};

export const useRanging = (options: UseRangingOptions = {}) => {
  const [snapshot, setSnapshot] = useState<BeaconRangingSnapshot | null>(null);

  const applySnapshot = (incoming: BeaconRangingSnapshot) => {
    setSnapshot((current) => selectRangingSnapshot(current, incoming));
  };

  const onSnapshot = useEffectEvent((incoming: BeaconRangingSnapshot) => {
    applySnapshot(incoming);
  });

  const onStarted = useEffectEvent((event: BeaconRangingStartedEvent) => {
    applySnapshot(snapshotFromRangingStarted(event));
    options.onStarted?.(event);
  });

  const onReceived = useEffectEvent((event: BeaconRangingReceivedEvent) => {
    options.onReceived?.(event);
  });

  const onTerminated = useEffectEvent((event: BeaconRangingTerminatedEvent) => {
    applySnapshot(snapshotFromRangingTerminated(event));
    options.onTerminated?.(event);
  });

  const onMeasurementParseError = useEffectEvent(
    (event: BeaconRangingMeasurementParseErrorEvent) => {
      options.onMeasurementParseError?.(event);
    },
  );

  useEffect(() => {
    const cancel = Effect.runCallback(
      observeRanging({
        onSnapshot,
        onStarted,
        onReceived,
        onTerminated,
        onMeasurementParseError,
        onDecodeError: (event, issues) => {
          // maybe expose?
          console.error(`Ranging ${event} event decode failed:`, issues);
        },
        onSnapshotError: (error) => {
          // maybe expose?
          console.error("Ranging snapshot query failed:", error);
        },
      }),
    );

    return () => {
      cancel();
    };
  }, []);

  return {
    snapshot,
    start: startRanging,
    stop: stopRanging,
  };
};
