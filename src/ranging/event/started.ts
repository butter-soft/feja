import { Schema } from "effect";
import { BeaconTargetSchema } from "../../beacon/target";
import { RangingExecutionIdSchema } from "../execution/id";
import { RangingRevisionSchema } from "../execution/revision";

export const BeaconRangingStartedEventSchema = Schema.Struct({
  revision: RangingRevisionSchema,
  target: BeaconTargetSchema,
  executionId: RangingExecutionIdSchema,
  startedAtMs: Schema.Finite,
});

export const decodeBeaconRangingStartedEvent = Schema.decodeUnknownResult(
  BeaconRangingStartedEventSchema,
);

export type BeaconRangingStartedEvent = typeof BeaconRangingStartedEventSchema.Type;
