import { Schema } from "effect";
import { describe, expect, it } from "vitest";

import { BeaconIdentifierSchema, BeaconIdentityValueSchema } from "./identity";

const isIdentifier = Schema.is(BeaconIdentifierSchema);
const isIdentityValue = Schema.is(BeaconIdentityValueSchema);

describe("BeaconIdentifierSchema", () => {
  it.each(["706E7568-5F70-656F-706C-655F74610032", "706e7568-5f70-656f-706c-655f74610032"])(
    "accepts device GUID %s regardless of letter case",
    (value) => {
      expect(isIdentifier(value)).toBe(true);
    },
  );

  it.each([
    "not-a-guid",
    "706E75685F70656F706C655F74610032",
    "706E7568-5F70-656F-706C-655F7461003Z",
    123,
    null,
    undefined,
  ])("rejects invalid identifier %j", (value) => {
    expect(isIdentifier(value)).toBe(false);
  });
});

describe("BeaconIdentityValueSchema", () => {
  it.each([0, 32768, 65535])("accepts UInt16 value %i", (value) => {
    expect(isIdentityValue(value)).toBe(true);
  });

  it.each([-1, 65536, 0.5, "1", null, undefined])("rejects invalid identity value %s", (value) => {
    expect(isIdentityValue(value)).toBe(false);
  });
});
