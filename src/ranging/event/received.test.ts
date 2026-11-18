import { Schema } from "effect";
import { describe, expect, it } from "vitest";
import { BeaconRangingReceivedEventSchema } from "./received";

const isReceivedEvent = Schema.is(BeaconRangingReceivedEventSchema);

const event = {
  target: { uuid: "706E7568-5F70-656F-706C-655F74610032" },
  executionId: "12345678-1234-4234-8234-123456789ABC",
  receivedAtMs: 1794800000000.5,
  measurements: [
    {
      uuid: "706E7568-5F70-656F-706C-655F74610032",
      major: 0,
      minor: 0,
      rssi: -65,
      accuracy: { status: "estimated", meters: 1.5 },
      proximity: { status: "determined", value: "near" },
      observedAtMs: 1794800000000.5,
    },
  ],
};

describe("BeaconRangingReceivedEventSchema", () => {
  it("accepts a complete event", () => expect(isReceivedEvent(event)).toBe(true));

  it("accepts an empty measurement batch", () =>
    expect(
      isReceivedEvent({
        ...event,
        measurements: [],
      }),
    ).toBe(true));

  it("rejects invalid receivedAtMs", () =>
    expect(
      isReceivedEvent({
        ...event,
        receivedAtMs: Infinity,
      }),
    ).toBe(false));
});
