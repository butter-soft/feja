import { Schema } from "effect";

/**
 * Beacon identifiers need GUID format validation only.
 * Observed device IDs can have non-IETF variant bits,
 * which Effect's stricter `isUUID()` rejects.
 */
export const BeaconIdentifierSchema = Schema.String.check(Schema.isGUID());

export const BeaconIdentityValueSchema = Schema.Int.check(
  Schema.isBetween({
    minimum: 0,
    maximum: 65535,
  }),
);
