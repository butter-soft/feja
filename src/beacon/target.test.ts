import { Schema } from "effect";
import { describe, expect, it } from "vitest";
import { BeaconTargetSchema } from "./target";

const isBeaconTarget = Schema.is(BeaconTargetSchema);

const uuid = "706E7568-5F70-656F-706C-655F74610032";

describe("BeaconTargetSchema", () => {
  it.each([
    { name: "UUID only", input: { uuid } },
    { name: "zero major", input: { uuid, major: 0 } },
    { name: "zero major and minor", input: { uuid, major: 0, minor: 0 } },
  ])("accepts $name", ({ input }) => expect(isBeaconTarget(input)).toBe(true));

  it.each([
    { name: "minor without major", input: { uuid, minor: 0 } },
    { name: "missing UUID", input: { major: 0 } },
  ])("rejects $name", ({ input }) => {
    expect(isBeaconTarget(input)).toBe(false);
  });
});
