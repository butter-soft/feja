import ExpoModulesCore
import Foundation

public class FejaModule: Module {
    private let manager = BeaconRangingManager()

    @MainActor
    private func start(
        _ record: BeaconTargetRecord
    ) -> Result<Void, BeaconCommandError> {
        record |> parseBeaconTargetRecord
            |> mapError(BeaconCommandError.invalidTarget)
            |> flatMap { target in
                self.connectEvents()
                return self.manager.start(target: target)
                    |> mapError(BeaconCommandError.startFailed)
            }
    }

    @MainActor
    private func stop(
        _ record: BeaconTargetRecord
    ) -> Result<Void, BeaconCommandError> {
        record |> parseBeaconTargetRecord
            |> mapError(BeaconCommandError.invalidTarget)
            |> map { target in
                self.manager.stop(target: target)
            }
    }

    @MainActor
    private func connectEvents() {
        manager.onStarted = { [weak self] revision, executionID, target, startedAt in
            self?.sendEvent(
                "onRangingStarted",
                makeBeaconRangingStartedPayload(
                    revision: revision,
                    target: target,
                    executionID: executionID,
                    startedAt: startedAt
                )
            )
        }

        manager.onReceived = { [weak self] executionID, target, batch in
            self?.sendEvent(
                "onRangingReceived",
                makeBeaconRangingReceivedPayload(
                    target: target,
                    executionID: executionID,
                    batch: batch
                )
            )
        }
        manager.onTerminated = { [weak self] revision, executionID, target, reason in
            let terminatedAt = Date()

            self?.sendEvent(
                "onRangingTerminated",
                makeBeaconRangingTerminatedPayload(
                    revision: revision,
                    target: target,
                    executionID: executionID,
                    terminatedAt: terminatedAt,
                    reason: reason
                )
            )
        }
    }

    public func definition() -> ModuleDefinition {
        Name("Feja")

        Events(
            "onRangingStarted",
            "onRangingReceived",
            "onRangingTerminated",
            "onRangingMeasurementParseError"
        )

        AsyncFunction("start") {
            (record: BeaconTargetRecord, promise: Promise) in
            MainActor.assumeIsolated {
                let result = self.start(record)
                promise.resolve(makeBeaconCommandResultPayload(result))
            }
        }.runOnQueue(.main)

        AsyncFunction("stop") {
            (record: BeaconTargetRecord, promise: Promise) in
            MainActor.assumeIsolated {
                let result = self.stop(record)
                promise.resolve(makeBeaconCommandResultPayload(result))
            }
        }.runOnQueue(.main)

        AsyncFunction("getSnapshot") {
            (promise: Promise) in
            MainActor.assumeIsolated {
                let snapshot = self.manager.snapshot
                promise.resolve(makeBeaconRangingSnapshotPayload(snapshot))
            }
        }.runOnQueue(.main)

        OnDestroy { [manager = self.manager] in
            Task { @MainActor in
                manager.onStarted = nil
                manager.onReceived = nil
                manager.onTerminated = nil

                if let target = manager.activeTarget {
                    manager.stop(target: target)
                }
            }
        }
    }
}
