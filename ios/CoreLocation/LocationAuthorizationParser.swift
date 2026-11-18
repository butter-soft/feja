import CoreLocation

func parseLocationAuthorization(_ status: CLAuthorizationStatus)
    -> Result<LocationAuthorizationState, LocationAuthorizationParseError>
{
    switch status {
    case .notDetermined:
        return .success(.notDetermined)
    case .restricted:
        return .success(.blocked(.restricted))
    case .denied:
        return .success(.blocked(.denied))
    case .authorizedWhenInUse:
        return .success(.authorized(.whenInUse))
    case .authorizedAlways:
        return .success(.authorized(.always))
    @unknown default:
        return .failure(
            LocationAuthorizationParseError.unsupportedAuthorizationStatus
        )
    }
}

func parseLocationAccuracyAuthorization(_ accuracy: CLAccuracyAuthorization)
    -> Result<LocationAccuracyAuthorization, LocationAuthorizationParseError>
{
    switch accuracy {
    case .fullAccuracy:
        return .success(.full)
    case .reducedAccuracy:
        return .success(.reduced)
    @unknown default:
        return .failure(
            LocationAuthorizationParseError.unsupportedAccuracyAuthorization
        )
    }
}

func parseLocationAuthorizationSnapshot(
    _ input: (
        authorization: CLAuthorizationStatus, accuracy: CLAccuracyAuthorization
    )
) -> Result<
    LocationAuthorizationSnapshot, LocationAuthorizationParseError
> {
    let authorization = parseLocationAuthorization(input.authorization)
    let accuracy = parseLocationAccuracyAuthorization(input.accuracy)

    return zip(authorization, accuracy)
        |> map { authorization, accuracy in
            LocationAuthorizationSnapshot(
                authorization: authorization,
                accuracy: accuracy
            )
        }
}
