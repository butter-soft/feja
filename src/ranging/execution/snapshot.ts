import { Schema } from "effect";
import { RangingRevisionSchema } from "./revision";
import { BeaconRangingStateSchema } from "./state";

export const BeaconRangingSnapshotSchema = Schema.Struct({
  revision: RangingRevisionSchema,
  state: BeaconRangingStateSchema,
});

export const decodeBeaconRangingSnapshot = Schema.decodeUnknownResult(BeaconRangingSnapshotSchema);

export const selectRangingSnapshot = (
  current: BeaconRangingSnapshot | null,
  incoming: BeaconRangingSnapshot,
) => {
  if (current === null || incoming.revision > current.revision) {
    return incoming;
  }

  return current;
};

export type BeaconRangingSnapshot = typeof BeaconRangingSnapshotSchema.Type;
