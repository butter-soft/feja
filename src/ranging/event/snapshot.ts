import type { BeaconRangingSnapshot } from "../execution/snapshot";
import type { BeaconRangingStartedEvent } from "./started";
import type { BeaconRangingTerminatedEvent } from "./terminated";

export const snapshotFromRangingStarted = (
  event: BeaconRangingStartedEvent,
): BeaconRangingSnapshot => ({
  revision: event.revision,
  state: {
    status: "running",
    target: event.target,
    executionId: event.executionId,
  },
});

export const snapshotFromRangingTerminated = (
  event: BeaconRangingTerminatedEvent,
): BeaconRangingSnapshot => ({
  revision: event.revision,
  state: { status: "stopped" },
});
