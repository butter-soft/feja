import { Schema } from "effect";
import { BeaconTargetSchema } from "../../beacon/target";
import { RangingExecutionIdSchema } from "../execution/id";
import { RangingRevisionSchema } from "../execution/revision";

export const BeaconRangingFailureSchema = Schema.Struct({
  domain: Schema.String,
  code: Schema.Int,
  message: Schema.String,
});

const RangingTerminationFields = {
  revision: RangingRevisionSchema,
  target: BeaconTargetSchema,
  executionId: RangingExecutionIdSchema,
  terminatedAtMs: Schema.Finite,
};

export const BeaconRangingTerminatedEventSchema = Schema.Union([
  Schema.Struct({
    ...RangingTerminationFields,
    reason: Schema.Literal("stopped"),
    error: Schema.optionalKey(Schema.Never),
  }),
  Schema.Struct({
    ...RangingTerminationFields,
    reason: Schema.Literal("failed"),
    error: BeaconRangingFailureSchema,
  }),
]);

export const decodeBeaconRangingTerminatedEvent = Schema.decodeUnknownResult(
  BeaconRangingTerminatedEventSchema,
);

export type BeaconRangingFailure = typeof BeaconRangingFailureSchema.Type;

export type BeaconRangingTerminatedEvent = typeof BeaconRangingTerminatedEventSchema.Type;
