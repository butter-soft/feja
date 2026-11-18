import { Schema } from "effect";
import { describe, expect, it } from "vitest";
import { BeaconMeasurementSchema } from "./measurement";

const isMeasurement = Schema.is(BeaconMeasurementSchema);

const measurement = {
  uuid: "706E7568-5F70-656F-706C-655F74610032",
  major: 0,
  minor: 0,
  rssi: -65,
  accuracy: { status: "estimated", meters: 1.5 },
  proximity: { status: "determined", value: "near" },
  observedAtMs: 1794800000000.5,
};

describe("BeaconMeasurementSchema", () => {
  it("accepts a complete measurement", () => {
    expect(isMeasurement(measurement)).toBe(true);
  });

  it("accepts zero RSSI", () => {
    expect(isMeasurement({ ...measurement, rssi: 0 })).toBe(true);
  });

  it.each([
    { field: "rssi", value: 0.5 },
    { field: "observedAtMs", value: Infinity },
  ])("rejects invalid $field", ({ field, value }) => {
    expect(isMeasurement({ ...measurement, [field]: value })).toBe(false);
  });
});
