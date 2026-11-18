import { Schema } from "effect";
import { describe, expect, it } from "vitest";
import { RangingExecutionIdSchema } from "./id";

const isExecutionId = Schema.is(RangingExecutionIdSchema);

describe("RangingExecutionIdSchema", () => {
  it("accepts UUID", () =>
    expect(isExecutionId("12345678-1234-4234-8234-123456789ABC")).toBe(true));

  it("rejects beacon GUID with non-IETF variant bits", () =>
    expect(isExecutionId("706E7568-5F70-656F-706C-655F74610032")).toBe(false));

  it("rejects unknown string", () => expect(isExecutionId("not-a-uuid")).toBe(false));
});
