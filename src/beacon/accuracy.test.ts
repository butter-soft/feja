import { Schema } from "effect";
import { describe, expect, it } from "vitest";
import { BeaconAccuracySchema } from "./accuracy";

const isAccuracy = Schema.is(BeaconAccuracySchema);

describe("BeaconAccuracySchema", () => {
  it.each([
    { name: "zero distance", input: { status: "estimated", meters: 0 } },
    { name: "fractional distance", input: { status: "estimated", meters: 1.5 } },
    { name: "undetermined", input: { status: "undetermined" } },
    {
      name: "invalid measurement",
      input: { status: "invalid", reason: "nonFiniteValue" },
    },
  ])("accepts $name", ({ input }) => {
    expect(isAccuracy(input)).toBe(true);
  });

  it.each([
    { name: "negative distance", input: { status: "estimated", meters: -1 } },
    { name: "NaN distance", input: { status: "estimated", meters: NaN } },
    { name: "infinite distance", input: { status: "estimated", meters: Infinity } },
    { name: "no distance", input: { status: "estimated" } },
    { name: "unsupported value", input: { status: "invalid", reason: "unsupportedValue" } },
    { name: "no reason", input: { status: "invalid" } },
    { name: "unknown status", input: { status: "???" } },
  ])("rejects $name", ({ input }) => expect(isAccuracy(input)).toBe(false));
});
