import { Schema } from "effect";
import { describe, expect, it } from "vitest";
import { BeaconRangingStartedEventSchema } from "./started";

const isStartedEvent = Schema.is(BeaconRangingStartedEventSchema);

const event = {
  revision: 1,
  target: { uuid: "706E7568-5F70-656F-706C-655F74610032" },
  executionId: "12345678-1234-4234-8234-123456789ABC",
  startedAtMs: 1794800000000.5,
};

describe("BeaconRangingStartedEventSchema", () => {
  it("accepts a complete event", () => expect(isStartedEvent(event)).toBe(true));

  it("rejects invalid startedAtMs", () =>
    expect(
      isStartedEvent({
        ...event,
        startedAtMs: Infinity,
      }),
    ).toBe(false));
});
