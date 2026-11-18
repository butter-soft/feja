import Foundation
import Testing

@testable import FejaDomain

struct RangingStartDecisionTests {
    private let uuidText = "706E7568-5F70-656F-706C-655F74610032"

    private var uuid: UUID {
        get throws {
            try #require(UUID(uuidString: uuidText))
        }
    }

    @Test
    func startsWhenStopped() throws {
        let target = BeaconTarget.uuid(try uuid)

        let decision = try decideBeaconRangingStart(
            (state: .stopped, requestedTarget: target)
        ).get()

        #expect(decision == .start)
    }

    @Test
    func keepsRunningForEqualTarget() throws {
        let active = BeaconTarget.uuid(try uuid)
        let requested = BeaconTarget.uuid(try uuid)
        let executionID = BeaconRangingExecutionID(rawValue: UUID())

        let decision = try decideBeaconRangingStart(
            (
                state: .running(target: active, executionID: executionID),
                requestedTarget: requested
            )
        ).get()

        #expect(decision == .keepRunning)
    }

    @Test
    func rejectsDifferentTargetAndPreservesConflictDetails() throws {
        let active = BeaconTarget.uuid(try uuid)
        let requested = BeaconTarget.uuidMajor(uuid: try uuid, major: 0)
        let executionID = BeaconRangingExecutionID(rawValue: UUID())

        let result = decideBeaconRangingStart(
            (
                state: .running(target: active, executionID: executionID),
                requestedTarget: requested
            )
        )

        switch result {
        case .failure(.targetConflict(let actualActive, let actualRequested)):
            #expect(actualActive == active)
            #expect(actualRequested == requested)
        default:
            Issue.record("Expected targetConflict")
        }
    }
}
