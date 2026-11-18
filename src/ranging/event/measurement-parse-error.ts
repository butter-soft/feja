import { Schema } from "effect";
import { BeaconTargetSchema } from "../../beacon/target";
import { RangingExecutionIdSchema } from "../execution/id";

export const BeaconRangingMeasurementParseErrorEventSchema = Schema.Struct({
  target: BeaconTargetSchema,
  executionId: RangingExecutionIdSchema,
  reportedAtMs: Schema.Finite,
  error: Schema.Struct({
    reason: Schema.Literal("invalidIdentifierLayout"),
    byteCounts: Schema.Array(Schema.Natural),
  }),
});

export const decodeBeaconRangingMeasurementParseErrorEvent = Schema.decodeUnknownResult(
  BeaconRangingMeasurementParseErrorEventSchema,
);

export type BeaconRangingMeasurementParseErrorEvent =
  typeof BeaconRangingMeasurementParseErrorEventSchema.Type;
