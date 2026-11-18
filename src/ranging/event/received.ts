import { Schema } from "effect";
import { BeaconTargetSchema } from "../../beacon/target";
import { BeaconMeasurementSchema } from "../../beacon/measurement";
import { RangingExecutionIdSchema } from "../execution/id";

export const BeaconRangingReceivedEventSchema = Schema.Struct({
  target: BeaconTargetSchema,
  executionId: RangingExecutionIdSchema,
  receivedAtMs: Schema.Finite,
  measurements: Schema.Array(BeaconMeasurementSchema),
});

export const decodeBeaconRangingReceivedEvent = Schema.decodeUnknownResult(
  BeaconRangingReceivedEventSchema,
);

export type BeaconRangingReceivedEvent = typeof BeaconRangingReceivedEventSchema.Type;
