import { Effect, Fiber } from "effect";
import { beforeEach, describe, expect, it, vi } from "vitest";

import type NativeModule from "../native/FejaModule";
import { getRangingSnapshot } from "./get-snapshot";
import { observeRanging } from "./observation";

const target = {
  uuid: "706E7568-5F70-656F-706C-655F74610032",
};

const executionId = "12345678-1234-4234-8234-123456789ABC";

const first = {
  target,
  executionId,
  receivedAtMs: 1794800000000,
  measurements: [],
};

const second = {
  ...first,
  receivedAtMs: first.receivedAtMs + 1000,
};

const { addListener } = vi.hoisted(() => ({
  addListener: vi.fn<typeof NativeModule.addListener>(),
}));

vi.mock("../native/FejaModule", () => ({
  default: {
    addListener,
  },
}));

vi.mock("./get-snapshot", () => ({
  getRangingSnapshot: vi.fn(),
}));

const makeObserver = () => ({
  onSnapshot: vi.fn(),
  onStarted: vi.fn(),
  onReceived: vi.fn(),
  onTerminated: vi.fn(),
  onMeasurementParseError: vi.fn(),
  onDecodeError: vi.fn(),
  onSnapshotError: vi.fn(),
});

beforeEach(() => {
  vi.resetAllMocks();
});

describe("observeRanging", () => {
  it("keeps another observer receiving after one session is interrupted", async () => {
    const listeners = new Map<string, Set<(payload: unknown) => void>>();

    addListener.mockImplementation((name, listener) => {
      const callbacks = listeners.get(name) ?? new Set();
      listeners.set(name, callbacks);
      callbacks.add(listener);

      return {
        remove: () => {
          callbacks.delete(listener);
        },
      };
    });

    const emitReceived = (payload: unknown) => {
      listeners.get("onRangingReceived")?.forEach((listener) => {
        listener(payload);
      });
    };

    vi.mocked(getRangingSnapshot).mockResolvedValue({
      revision: 1,
      state: {
        status: "running",
        target,
        executionId,
      },
    });

    const observerA = makeObserver();
    const observerB = makeObserver();

    const fiberA = Effect.runFork(observeRanging(observerA));
    const fiberB = Effect.runFork(observeRanging(observerB));

    try {
      await vi.waitFor(() => {
        expect(observerA.onSnapshot).toHaveBeenCalledTimes(1);
        expect(observerB.onSnapshot).toHaveBeenCalledTimes(1);
      });

      emitReceived(first);

      expect(observerA.onReceived).toHaveBeenCalledExactlyOnceWith(first);
      expect(observerB.onReceived).toHaveBeenCalledExactlyOnceWith(first);

      await Effect.runPromise(Fiber.interrupt(fiberA));

      emitReceived(second);

      expect(observerA.onReceived).toHaveBeenCalledTimes(1);
      expect(observerB.onReceived).toHaveBeenCalledTimes(2);
      expect(observerB.onReceived).toHaveBeenLastCalledWith(second);
    } finally {
      await Effect.runPromise(Fiber.interruptAll([fiberA, fiberB]));
    }
  });
});
