import { Schema } from "effect";

export const BeaconProximitySchema = Schema.Union([
  Schema.Struct({
    status: Schema.Literal("determined"),
    value: Schema.Literals(["immediate", "near", "far"]),
  }),
  Schema.Struct({
    status: Schema.Literal("undetermined"),
  }),
  Schema.Struct({
    status: Schema.Literal("invalid"),
    reason: Schema.Literal("unsupportedValue"),
    rawValue: Schema.Int,
  }),
  Schema.Struct({
    status: Schema.Literal("invalid"),
    reason: Schema.Literal("nonFiniteDistance"),
  }),
]);

export type BeaconProximity = typeof BeaconProximitySchema.Type;
