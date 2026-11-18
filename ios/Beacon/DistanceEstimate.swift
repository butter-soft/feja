enum BeaconDistanceEstimateParseError: Error {
    case nonFiniteValue // infinite + NaN
    case negativeValue
}

nonisolated struct BeaconDistanceEstimate {
    let meters: Double

    private init(meters: Double) {
        self.meters = meters
    }

    static func parse(_ input: Double) -> Result<
        BeaconDistanceEstimate, BeaconDistanceEstimateParseError
    > {
        guard input.isFinite else {
            return .failure(.nonFiniteValue)
        }

        guard input >= 0 else {
            return .failure(.negativeValue)
        }

        return .success(BeaconDistanceEstimate(meters: input))
    }
}
