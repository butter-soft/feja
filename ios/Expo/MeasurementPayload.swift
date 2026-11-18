import Foundation

func makeBeaconAccuracyPayload(
    _ accuracy: Result<BeaconAccuracy, BeaconAccuracyParseError>
) -> [String: Any] {
    switch accuracy {
    case .success(.estimated(let estimate)):
        return [
            "status": "estimated",
            "meters": estimate.meters,
        ]
    case .success(.undetermined):
        return [
            "status": "undetermined"
        ]
    case .failure(.nonFiniteValue):
        return [
            "status": "invalid",
            "reason": "nonFiniteValue",
        ]
    }
}

func makeBeaconProximityPayload(
    _ proximity: Result<BeaconProximity, BeaconProximityParseError>
) -> [String: Any] {
    switch proximity {
    case .success(let value):
        switch value {
        case .unknown:
            return [
                "status": "undetermined"
            ]
        case .immediate, .near, .far:
            return [
                "status": "determined",
                "value": value.rawValue,
            ]
        }
    case .failure(.unsupportedValue(let rawValue)):
        return [
            "status": "invalid",
            "reason": "unsupportedValue",
            "rawValue": rawValue,
        ]
    }
}

func makeBeaconMeasurementPayload(_ measurement: BeaconMeasurement) -> [String:
    Any]
{
    [
        "uuid": measurement.uuid.uuidString,
        "major": Int(measurement.major),
        "minor": Int(measurement.minor),
        "rssi": measurement.rssi,
        "proximity": makeBeaconProximityPayload(measurement.proximity),
        "accuracy": makeBeaconAccuracyPayload(measurement.accuracy),
        "observedAtMs": measurement.observedAt.timeIntervalSince1970 * 1000,
    ]
}
