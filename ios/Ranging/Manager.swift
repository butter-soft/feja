import Foundation

@MainActor
final class BeaconRangingManager {
    private static let maxJavaScriptSafeInteger: UInt64 = (1 << 53) - 1
    
    private var activeRanger: BeaconRanger?
    private var revision: UInt64 = 0

    var onStarted: ((UInt64, BeaconRangingExecutionID, BeaconTarget, Date) -> Void)?
    var onReceived:
        ((BeaconRangingExecutionID, BeaconTarget, RangingBatch) -> Void)?
    var onTerminated:
        (
            (UInt64, BeaconRangingExecutionID, BeaconTarget, BeaconRangingTermination)
                -> Void
        )?

    var activeTarget: BeaconTarget? {
        guard let ranger = activeRanger, ranger.isRanging else {
            return nil
        }

        return ranger.target
    }

    var isRanging: Bool {
        activeTarget != nil
    }

    var state: BeaconRangingState {
        guard let ranger = activeRanger, ranger.isRanging else {
            return .stopped
        }

        return .running(target: ranger.target, executionID: ranger.executionID)
    }
    
    var snapshot: BeaconRangingSnapshot {
        BeaconRangingSnapshot(revision: revision, state: state)
    }

    nonisolated init() {}
    
    private func advanceRevision() {
        precondition(
            revision < Self.maxJavaScriptSafeInteger,
            "Ranging revision exceeded JavaScript's safe integer range."
        )

        revision += 1
    }

    private func connect(_ ranger: BeaconRanger) {
        let target = ranger.target

        ranger.onReceived = { [weak self] executionID, batch in
            guard let self, self.activeRanger?.executionID == executionID else {
                return
            }

            self.onReceived?(executionID, target, batch)
        }

        ranger.onTerminated = { [weak self] executionID, reason in
            guard let self, self.activeRanger?.executionID == executionID else {
                return
            }

            self.activeRanger = nil
            advanceRevision()
            self.onTerminated?(self.revision, executionID, target, reason)
        }
    }

    private func startNewExecution(target: BeaconTarget) -> Result<
        Void, BeaconRangingStartError
    > {
        let executionID = BeaconRangingExecutionID(rawValue: UUID())
        let ranger = BeaconRanger(executionID: executionID, target: target)

        connect(ranger)
        activeRanger = ranger

        let result = ranger.start()

        switch result {
        case .success:
            advanceRevision()
            onStarted?(revision, executionID, target, Date())
        case .failure:
            activeRanger = nil
        }

        return result
    }

    private func executeStart(
        _ decision: BeaconRangingStartDecision,
        target: BeaconTarget
    ) -> Result<Void, BeaconRangingStartError> {
        switch decision {
        case .start:
            return startNewExecution(target: target)
        case .keepRunning:
            return .success(())
        }
    }

    func start(target: BeaconTarget) -> Result<Void, BeaconRangingStartError> {
        let input = (state: state, requestedTarget: target)

        return input |> decideBeaconRangingStart
            |> flatMap { decision in
                self.executeStart(decision, target: target)
            }
    }

    func stop(target: BeaconTarget) {
        guard let ranger = activeRanger, ranger.target == target else {
            return
        }

        ranger.stop()
    }
}
