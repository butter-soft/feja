enum BeaconProximity: String, Codable {
    case unknown
    case immediate
    case near
    case far
}

enum BeaconProximityParseError: Error {
    case unsupportedValue(rawValue: Int)
}
