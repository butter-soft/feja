import Foundation

func makeBeaconRangingStartedPayload(
    revision: UInt64,
    target: BeaconTarget,
    executionID: BeaconRangingExecutionID,
    startedAt: Date
) -> [String: Any] {
    [
        "revision": Double(revision),
        "target": makeBeaconTargetPayload(target),
        "executionId": executionID.rawValue.uuidString,
        "startedAtMs": startedAt.timeIntervalSince1970 * 1000,
    ]
}

func makeBeaconRangingReceivedPayload(
    target: BeaconTarget,
    executionID: BeaconRangingExecutionID,
    batch: RangingBatch
) -> [String: Any] {
    [
        "target": makeBeaconTargetPayload(target),
        "executionId": executionID.rawValue.uuidString,
        "receivedAtMs":
            batch.receivedAt.timeIntervalSince1970 * 1000,
        "measurements":
            batch.measurements.map(makeBeaconMeasurementPayload),
    ]
}

func makeBeaconRangingTerminatedPayload(
    revision: UInt64,
    target: BeaconTarget,
    executionID: BeaconRangingExecutionID,
    terminatedAt: Date,
    reason: BeaconRangingTermination
) -> [String: Any] {
    let outcome: [String: Any] =
        switch reason {
        case .stopped:
            [
                "reason": "stopped"
            ]

        case .failed(let failure):
            [
                "reason": "failed",
                "error": [
                    "domain": failure.domain,
                    "code": failure.code,
                    "message": failure.message,
                ],
            ]
        }

    let execution: [String: Any] = [
        "revision": Double(revision),
        "target": makeBeaconTargetPayload(target),
        "executionId": executionID.rawValue.uuidString,
        "terminatedAtMs":
            terminatedAt.timeIntervalSince1970 * 1000,
    ]

    return execution.merging(outcome) { _, new in new }
}
