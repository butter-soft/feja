import { Schema } from "effect";
import { describe, expect, it } from "vitest";
import { NativeCommandResultSchema } from "./command-result";

const isCommandResult = Schema.is(NativeCommandResultSchema);

const failure = {
  code: "ERR_IBEACON_AUTHORIZATION_REQUIRED",
  message: "Authorization required",
  details: {},
};

describe("NativeCommandResultSchema", () => {
  describe("valid payloads", () => {
    it("accepts success", () => expect(isCommandResult({ status: "success" })).toBe(true));
    it("accepts a complete failure", () =>
      expect(isCommandResult({ status: "failure", error: failure })).toBe(true));
    it("accepts a target conflict", () => {
      expect(
        isCommandResult({
          status: "failure",
          error: {
            code: "ERR_IBEACON_TARGET_CONFLICT",
            message: "Another target is running",
            details: {
              active: {
                uuid: "706E7568-5F70-656F-706C-655F74610032",
              },
              requested: {
                uuid: "706E7568-5F70-656F-706C-655F74610032",
                major: 0,
              },
            },
          },
        }),
      ).toBe(true);
    });

    it.each(["Bluetooth scan failed", null])(
      "accepts a library failure with message %j",
      (message) => {
        expect(
          isCommandResult({
            status: "failure",
            error: {
              code: "ERR_IBEACON_RANGING_START_FAILED",
              message: "Could not start ranging",
              details: {
                exceptionType: "TestException",
                message,
              },
            },
          }),
        ).toBe(true);
      },
    );
    it("accepts a cleanup failure with the original start error", () => {
      expect(
        isCommandResult({
          status: "failure",
          error: {
            code: "ERR_IBEACON_RANGING_START_CLEANUP_FAILED",
            message: "Cleanup failed after start was rejected",
            details: {
              startError: failure,
              cleanupFailure: {
                exceptionType: "TestException",
                message: "Could not release ranging resources",
              },
            },
          },
        }),
      ).toBe(true);
    });
  });
  describe("invalid payloads", () => {
    it("rejects failure without error", () => {
      expect(isCommandResult({ status: "failure" })).toBe(false);
    });
    it("rejects a cleanup failure with an invalid start error", () => {
      expect(
        isCommandResult({
          status: "failure",
          error: {
            code: "ERR_IBEACON_RANGING_START_CLEANUP_FAILED",
            message: "Cleanup failed after start was rejected",
            details: {
              startError: {},
              cleanupFailure: {
                exceptionType: "TestException",
                message: "Could not release ranging resources",
              },
            },
          },
        }),
      ).toBe(false);
    });
  });
});
