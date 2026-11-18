import { Schema } from "effect";

export const BeaconAccuracySchema = Schema.Union([
  Schema.Struct({
    status: Schema.Literal("estimated"),
    meters: Schema.Finite.check(Schema.isGreaterThanOrEqualTo(0)),
  }),
  Schema.Struct({
    status: Schema.Literal("undetermined"),
  }),
  Schema.Struct({
    status: Schema.Literal("invalid"),
    reason: Schema.Literal("nonFiniteValue"),
  }),
]);

export type BeaconAccuracy = typeof BeaconAccuracySchema.Type;
