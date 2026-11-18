import { Schema } from "effect";
import { describe, expect, it } from "vitest";
import { BeaconRangingMeasurementParseErrorEventSchema } from "./measurement-parse-error";

const isMeasurementParseErrorEvent = Schema.is(BeaconRangingMeasurementParseErrorEventSchema);

const event = {
  target: { uuid: "706E7568-5F70-656F-706C-655F74610032" },
  executionId: "12345678-1234-4234-8234-123456789ABC",
  reportedAtMs: 1794800000000.5,
  error: {
    reason: "invalidIdentifierLayout",
    byteCounts: [0, 2, 15],
  },
};

describe("BeaconRangingMeasurementParseErrorEventSchema", () => {
  it("accepts a complete event", () => expect(isMeasurementParseErrorEvent(event)).toBe(true));

  it.each([
    {
      name: "negative byteCount",
      input: { ...event, error: { ...event.error, byteCounts: [-1, 2, 15] } },
    },
    {
      name: "decimal byteCount",
      input: { ...event, error: { ...event.error, byteCounts: [0.5, 2, 15] } },
    },
    {
      name: "unknown error reason",
      input: { ...event, error: { ...event.error, reason: "???" } },
    },
    {
      name: "infinite reportedAtMs",
      input: { ...event, reportedAtMs: Infinity },
    },
  ])("rejects $name", ({ input }) => expect(isMeasurementParseErrorEvent(input)).toBe(false));
});
