private func makeErrorPayload(
    code: String,
    message: String,
    details: [String: Any] = [:]
) -> [String: Any] {
    [
        "code": code,
        "message": message,
        "details": details
    ]
}

func makeBeaconTargetParseErrorPayload(
    _ error: BeaconTargetParseError
) -> [String: Any] {
    switch error {
    case .invalidUUID:
        return makeErrorPayload(
            code: "ERR_IBEACON_INVALID_UUID",
            message: "UUID must be a valid UUID string."
        )
    case .invalidIdentityValue:
        return makeErrorPayload(
            code: "ERR_IBEACON_INVALID_IDENTITY_VALUE",
            message: "Major and minor must be integers between 0 and 65535."
        )
    case .minorWithoutMajor:
        return makeErrorPayload(
            code: "ERR_IBEACON_MINOR_WITHOUT_MAJOR",
            message: "Minor requires major."
        )
    }
}

func makeLocationAuthorizationParseErrorPayload(
    _ error: LocationAuthorizationParseError
) -> [String: Any] {
    switch error {
    case .unsupportedAuthorizationStatus:
        return makeErrorPayload(
            code: "ERR_IBEACON_UNSUPPORTED_AUTHORIZATION_STATUS",
            message: "The location authorization status is unsupported."
        )
    case .unsupportedAccuracyAuthorization:
        return makeErrorPayload(
            code: "ERR_IBEACON_UNSUPPORTED_ACCURACY_AUTHORIZATION",
            message: "The location accuracy authorization is unsupported."
        )
    }
}

func makeBeaconRangingStartErrorPayload(
    _ error: BeaconRangingStartError
) -> [String: Any] {
    switch error {
    case .targetConflict(let active, let requested):
        return makeErrorPayload(
            code: "ERR_IBEACON_TARGET_CONFLICT",
            message: "Ranging is already running for a different target.",
            details: [
                "active": makeBeaconTargetPayload(active),
                "requested": makeBeaconTargetPayload(requested)
            ]
        )

    case .unavailable:
        return makeErrorPayload(
            code: "ERR_IBEACON_RANGING_UNAVAILABLE",
            message: "Beacon ranging is unavailable."
        )

    case .executionTerminated:
        return makeErrorPayload(
            code: "ERR_IBEACON_EXECUTION_TERMINATED",
            message: "A terminated ranging execution cannot be restarted."
        )

    case .authorizationParsingFailed(let reason):
        return makeLocationAuthorizationParseErrorPayload(reason)

    case .authorizationRequired:
        return makeErrorPayload(
            code: "ERR_IBEACON_AUTHORIZATION_REQUIRED",
            message: "Location authorization is required."
        )

    case .fullAccuracyRequired:
        return makeErrorPayload(
            code: "ERR_IBEACON_FULL_ACCURACY_REQUIRED",
            message: "Full location accuracy is required."
        )
    }
}

func makeBeaconCommandErrorPayload(
    _ error: BeaconCommandError
) -> [String: Any] {
    switch error {
    case .invalidTarget(let reason):
        return makeBeaconTargetParseErrorPayload(reason)

    case .startFailed(let reason):
        return makeBeaconRangingStartErrorPayload(reason)
    }
}
