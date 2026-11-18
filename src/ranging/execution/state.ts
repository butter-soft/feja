import { Schema } from "effect";

import { BeaconTargetSchema } from "../../beacon/target";
import { RangingExecutionIdSchema } from "./id";

export const BeaconRangingStateSchema = Schema.Union([
  Schema.Struct({
    status: Schema.Literal("stopped"),
    target: Schema.optionalKey(Schema.Never),
    executionId: Schema.optionalKey(Schema.Never),
  }),
  Schema.Struct({
    status: Schema.Literal("running"),
    target: BeaconTargetSchema,
    executionId: RangingExecutionIdSchema,
  }),
]);

export const decodeBeaconRangingState = Schema.decodeUnknownResult(BeaconRangingStateSchema);

export type BeaconRangingState = typeof BeaconRangingStateSchema.Type;
