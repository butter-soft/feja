import CoreLocation

func parseBeaconProximity(_ input: CLProximity) -> Result<
    BeaconProximity, BeaconProximityParseError
> {
    switch input {
    case .unknown:
        return .success(.unknown)
    case .immediate:
        return .success(.immediate)
    case .near:
        return .success(.near)
    case .far:
        return .success(.far)
    @unknown default:
        return .failure(.unsupportedValue(rawValue: input.rawValue))
    }
}
