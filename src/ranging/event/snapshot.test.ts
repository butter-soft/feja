import { describe, expect, it } from "vitest";
import { snapshotFromRangingStarted, snapshotFromRangingTerminated } from "./snapshot";
import type { BeaconRangingSnapshot } from "../execution/snapshot";

const running = {
  revision: 1,
  state: {
    status: "running",
    target: { uuid: "706E7568-5F70-656F-706C-655F74610032" },
    executionId: "12345678-1234-4234-8234-123456789ABC",
  },
} satisfies BeaconRangingSnapshot;

describe("snapshotFromRangingStarted", () => {
  it("preserves the revision and execution as running state", () => {
    expect(
      snapshotFromRangingStarted({
        revision: running.revision,
        target: running.state.target,
        executionId: running.state.executionId,
        startedAtMs: 1794800000000.5,
      }),
    ).toEqual(running);
  });
});

describe("snapshotFromRangingTerminated", () => {
  it("projects a termination into stopped state with its revision", () => {
    expect(
      snapshotFromRangingTerminated({
        revision: 2,
        target: running.state.target,
        executionId: running.state.executionId,
        terminatedAtMs: 1794800001000.5,
        reason: "failed",
        error: {
          domain: "test",
          code: 1,
          message: "Ranging failed",
        },
      }),
    ).toEqual({
      revision: 2,
      state: { status: "stopped" },
    });
  });
});
