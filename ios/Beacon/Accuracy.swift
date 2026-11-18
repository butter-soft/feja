enum BeaconAccuracyParseError: Error {
    case nonFiniteValue
}

enum BeaconAccuracy {
    case estimated(BeaconDistanceEstimate)
    case undetermined
}

func parseBeaconAccuracy(_ input: Double) -> Result<
    BeaconAccuracy, BeaconAccuracyParseError
> {
    input |> BeaconDistanceEstimate.parse |> map(BeaconAccuracy.estimated)
        |> flatMapError { error in
            switch error {
            case .negativeValue:
                return .success(.undetermined)
            case .nonFiniteValue:
                return .failure(BeaconAccuracyParseError.nonFiniteValue)
            }

        }
}
