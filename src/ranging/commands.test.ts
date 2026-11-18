import { beforeEach, describe, expect, it, vi } from "vitest";

import type NativeModule from "../native/FejaModule";
import { BeaconCommandError, BeaconResponseError } from "./command-errors";
import { startRanging } from "./commands";

const { start } = vi.hoisted(() => ({
  start: vi.fn<typeof NativeModule.start>(),
}));

vi.mock("../native/FejaModule", () => ({
  default: { start },
}));

const target = {
  uuid: "706E7568-5F70-656F-706C-655F74610032",
};

beforeEach(() => {
  vi.resetAllMocks();
});

describe("startRanging", () => {
  it("rejects a command failure and preserves its details", async () => {
    const failure = {
      code: "ERR_IBEACON_AUTHORIZATION_REQUIRED",
      message: "Authorization required",
      details: {},
    };

    start.mockResolvedValue({
      status: "failure",
      error: failure,
    });

    const promise = startRanging(target);

    await expect(promise).rejects.toBeInstanceOf(BeaconCommandError);
    await expect(promise).rejects.toMatchObject({
      failure,
      message: failure.message,
    });
  });

  it("rejects a malformed response with payload diagnostics", async () => {
    start.mockResolvedValue(null);

    const promise = startRanging(target);

    await expect(promise).rejects.toBeInstanceOf(BeaconResponseError);
    await expect(promise).rejects.toMatchObject({
      issues: expect.arrayContaining([
        {
          path: [],
          message: expect.any(String),
        },
      ]),
    });
  });
});
