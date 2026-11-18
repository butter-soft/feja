enum BeaconRangingStartDecision {
    case start
    case keepRunning
}

enum BeaconRangingStartError: Error {
    case targetConflict(active: BeaconTarget, requested: BeaconTarget)
    case unavailable
    case executionTerminated
    case authorizationParsingFailed(LocationAuthorizationParseError)
    case authorizationRequired
    case fullAccuracyRequired
}

func decideRangerStart(
    _ input: (
        phase: BeaconRangingExecutionPhase,
        isAvailable: Bool,
        authorization: Result<
            LocationAuthorizationSnapshot, LocationAuthorizationParseError
        >
    )
) -> Result<BeaconRangingStartDecision, BeaconRangingStartError> {
    switch (input.phase, input.isAvailable) {
    case (.notStarted, true):
        return input.authorization
            |> mapError(BeaconRangingStartError.authorizationParsingFailed)
            |> flatMap(decideAuthorizedRangingStart)

    case (.notStarted, false):
        return .failure(.unavailable)

    case (.running, _):
        return .success(.keepRunning)

    case (.terminated, _):
        return .failure(.executionTerminated)
    }
}

func decideAuthorizedRangingStart(_ snapshot: LocationAuthorizationSnapshot)
    -> Result<BeaconRangingStartDecision, BeaconRangingStartError>
{
    switch (snapshot.authorization, snapshot.accuracy) {
    case (.authorized(_), .full):
        return .success(.start)
    case (.authorized(_), .reduced):
        return .failure(.fullAccuracyRequired)
    case (.notDetermined, _), (.blocked(_), _):
        return .failure(.authorizationRequired)
    }
}

func decideBeaconRangingStart(
    _ input: (state: BeaconRangingState, requestedTarget: BeaconTarget)
) -> Result<BeaconRangingStartDecision, BeaconRangingStartError> {
    switch input.state {
    case .stopped:
        return .success(.start)
    case .running(let activeTarget, _):
        guard activeTarget == input.requestedTarget else {
            return .failure(
                .targetConflict(
                    active: activeTarget,
                    requested: input.requestedTarget
                )
            )
        }

        return .success(.keepRunning)
    }
}
