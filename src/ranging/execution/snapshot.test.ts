import { describe, expect, it } from "vitest";
import { selectRangingSnapshot, type BeaconRangingSnapshot } from "./snapshot";

const stopped = {
  revision: 0,
  state: { status: "stopped" },
} satisfies BeaconRangingSnapshot;

const running = {
  revision: 1,
  state: {
    status: "running",
    target: { uuid: "706E7568-5F70-656F-706C-655F74610032" },
    executionId: "12345678-1234-4234-8234-123456789ABC",
  },
} satisfies BeaconRangingSnapshot;

describe("selectRangingSnapshot", () => {
  it("accepts an initial snapshot with revision zero", () => {
    expect(selectRangingSnapshot(null, stopped)).toBe(stopped);
  });

  it("accepts a newer snapshot", () => {
    expect(selectRangingSnapshot(stopped, running)).toBe(running);
  });

  it("preserves the current snapshot for an equal revision", () => {
    const incoming = {
      ...running,
      state: { ...running.state },
    };

    expect(selectRangingSnapshot(running, incoming)).toBe(running);
  });

  it("ignores an older snapshot arriving after a start event", () => {
    expect(selectRangingSnapshot(running, stopped)).toBe(running);
  });
});
