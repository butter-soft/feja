import { Schema } from "effect";
import { BeaconAccuracySchema } from "./accuracy";
import { BeaconIdentifierSchema, BeaconIdentityValueSchema } from "./identity";
import { BeaconProximitySchema } from "./proximity";

export const BeaconMeasurementSchema = Schema.Struct({
  uuid: BeaconIdentifierSchema,
  major: BeaconIdentityValueSchema,
  minor: BeaconIdentityValueSchema,
  rssi: Schema.Int,
  proximity: BeaconProximitySchema,
  accuracy: BeaconAccuracySchema,
  observedAtMs: Schema.Finite,
});

export type BeaconMeasurement = typeof BeaconMeasurementSchema.Type;
