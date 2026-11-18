import Foundation

/// Uses `Double` to preserve JavaScript `number` values before parsing
struct BeaconTargetInput {
    let uuid: String
    let major: Double?
    let minor: Double?
}

enum BeaconTargetParseError: Error {
    case invalidUUID
    case invalidIdentityValue
    case minorWithoutMajor
}

func parseBeaconUUID(_ input: String) -> Result<UUID, BeaconTargetParseError> {
    guard let uuid = UUID(uuidString: input) else {
        return .failure(.invalidUUID)
    }

    return .success(uuid)
}

func parseBeaconIdentityValue(_ input: Double) -> Result<
    UInt16, BeaconTargetParseError
> {
    guard let value = UInt16(exactly: input) else {
        return .failure(.invalidIdentityValue)
    }

    return .success(value)
}

func parseOptionalBeaconIdentityValue(_ input: Double?) -> Result<
    UInt16?, BeaconTargetParseError
> {
    guard let input else {
        return .success(nil)
    }

    return parseBeaconIdentityValue(input).map { Optional.some($0) }
}

func parseBeaconTargetFields(_ input: BeaconTargetInput) -> Result<
    (uuid: UUID, major: UInt16?, minor: UInt16?), BeaconTargetParseError
> {
    let uuid = parseBeaconUUID(input.uuid)
    let major = parseOptionalBeaconIdentityValue(input.major)
    let minor = parseOptionalBeaconIdentityValue(input.minor)

    let makeFields = curry {
        (uuid: UUID, major: UInt16?, minor: UInt16?) in
        (uuid: uuid, major: major, minor: minor)
    }

    return uuid |> map(makeFields) |> apply(major) |> apply(minor)
}

func assembleBeaconTarget(
    _ fields: (uuid: UUID, major: UInt16?, minor: UInt16?)
) -> Result<BeaconTarget, BeaconTargetParseError> {
    parseBeaconTargetIdentity(
        uuid: fields.uuid,
        major: fields.major,
        minor: fields.minor
    )
}

func parseBeaconTargetIdentity(uuid: UUID, major: UInt16?, minor: UInt16?)
    -> Result<BeaconTarget, BeaconTargetParseError>
{
    switch (major, minor) {
    case (nil, nil):
        return .success(.uuid(uuid))
    case (.some(let major), nil):
        return .success(.uuidMajor(uuid: uuid, major: major))
    case (.some(let major), .some(let minor)):
        return .success(.uuidMajorMinor(uuid: uuid, major: major, minor: minor))
    case (nil, .some(_)):
        return .failure(.minorWithoutMajor)
    }
}

func parseBeaconTargetInput(_ input: BeaconTargetInput) -> Result<
    BeaconTarget, BeaconTargetParseError
> {
    input |> parseBeaconTargetFields |> flatMap(assembleBeaconTarget)
}
