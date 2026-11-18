import { Schema } from "effect";
import { describe, expect, it } from "vitest";
import { BeaconRangingTerminatedEventSchema } from "./terminated";

const isTerminatedEvent = Schema.is(BeaconRangingTerminatedEventSchema);

const base = {
  revision: 1,
  target: { uuid: "706E7568-5F70-656F-706C-655F74610032" },
  executionId: "12345678-1234-4234-8234-123456789ABC",
  terminatedAtMs: 1794800000000.5,
};

const error = {
  domain: "test",
  code: 1,
  message: "Ranging failed",
};

describe("BeaconRangingTerminatedEventSchema", () => {
  it("accepts a stopped event", () =>
    expect(
      isTerminatedEvent({
        ...base,
        reason: "stopped",
      }),
    ).toBe(true));

  it("accepts a failed event", () =>
    expect(
      isTerminatedEvent({
        ...base,
        reason: "failed",
        error,
      }),
    ).toBe(true));

  it("rejects a stopped event with error", () =>
    expect(
      isTerminatedEvent({
        ...base,
        reason: "stopped",
        error,
      }),
    ).toBe(false));

  it("rejects a failed event without error", () =>
    expect(
      isTerminatedEvent({
        ...base,
        reason: "failed",
      }),
    ).toBe(false));
});
