import Foundation

func makeBeaconRangingStatePayload(_ state: BeaconRangingState) -> [String: Any]
{
    switch state {
    case .stopped:
        return [
            "status": "stopped"
        ]
    case .running(let target, let executionID):
        return [
            "status": "running",
            "target": makeBeaconTargetPayload(target),
            "executionId": executionID.rawValue.uuidString,
        ]
    }
}
