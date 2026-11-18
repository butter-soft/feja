import { Schema } from "effect";
import { describe, expect, it } from "vitest";
import { BeaconRangingStateSchema } from "./state";

const isState = Schema.is(BeaconRangingStateSchema);

const target = {
  uuid: "706E7568-5F70-656F-706C-655F74610032",
};

const running = {
  status: "running",
  target,
  executionId: "12345678-1234-4234-8234-123456789ABC",
};

describe("BeaconRangingStateSchema", () => {
  it.each([
    { name: "stopped", input: { status: "stopped" } },
    { name: "running", input: running },
  ])("accepts $name", ({ input }) => expect(isState(input)).toBe(true));

  it.each([
    {
      name: "stopped with target",
      input: { status: "stopped", target },
    },
    {
      name: "stopped with execution ID",
      input: { status: "stopped", executionId: running.executionId },
    },
    {
      name: "running without target",
      input: { status: "running", executionId: running.executionId },
    },
    {
      name: "running without execution ID",
      input: { status: "running", target },
    },
  ])("rejects $name", ({ input }) => expect(isState(input)).toBe(false));
});
