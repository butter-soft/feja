import { Schema } from "effect";

import { BeaconIdentityValueSchema, BeaconIdentifierSchema } from "./identity";

export const BeaconTargetSchema = Schema.Union([
  Schema.Struct({
    uuid: BeaconIdentifierSchema,
    major: Schema.optionalKey(Schema.Never),
    minor: Schema.optionalKey(Schema.Never),
  }),
  Schema.Struct({
    uuid: BeaconIdentifierSchema,
    major: BeaconIdentityValueSchema,
    minor: Schema.optionalKey(Schema.Never),
  }),
  Schema.Struct({
    uuid: BeaconIdentifierSchema,
    major: BeaconIdentityValueSchema,
    minor: BeaconIdentityValueSchema,
  }),
]);

export type BeaconTarget = typeof BeaconTargetSchema.Type;
export type BeaconTargetInput = typeof BeaconTargetSchema.Encoded;
