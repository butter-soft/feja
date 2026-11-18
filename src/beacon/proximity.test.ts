import { Schema } from "effect";
import { describe, expect, it } from "vitest";
import { BeaconProximitySchema } from "./proximity";

const isProximity = Schema.is(BeaconProximitySchema);

describe("BeaconProximitySchema", () => {
  it.each([
    { name: "immediate proximity", input: { status: "determined", value: "immediate" } },
    { name: "near proximity", input: { status: "determined", value: "near" } },
    { name: "far proximity", input: { status: "determined", value: "far" } },
    { name: "undetermined", input: { status: "undetermined" } },
    {
      name: "unsupported value",
      input: { status: "invalid", reason: "unsupportedValue", rawValue: -1 },
    },
    {
      name: "non-finite distance",
      input: { status: "invalid", reason: "nonFiniteDistance" },
    },
  ])("accepts $name", ({ input }) => {
    expect(isProximity(input)).toBe(true);
  });

  it.each([
    { name: "proximity not present", input: { status: "determined" } },
    { name: "unknown proximity", input: { status: "determined", value: "not-so-far" } },
    { name: "rawValue not present", input: { status: "invalid", reason: "unsupportedValue" } },
    {
      name: "decimal rawValue",
      input: { status: "invalid", reason: "unsupportedValue", rawValue: 0.5 },
    },
    {
      name: "non-numeric rawValue",
      input: { status: "invalid", reason: "unsupportedValue", rawValue: "raw" },
    },
    {
      name: "reason not present",
      input: { status: "invalid" },
    },
    {
      name: "unknown reason",
      input: { status: "invalid", reason: "???", rawValue: -1 },
    },
    {
      name: "unknown status",
      input: { status: "???" },
    },
  ])("rejects $name", ({ input }) => expect(isProximity(input)).toBe(false));
});
