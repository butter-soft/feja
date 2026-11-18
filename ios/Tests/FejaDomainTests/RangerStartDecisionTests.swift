import Testing

@testable import FejaDomain

struct RangerStartDecisionTests {
    private var authorization: Result<
        LocationAuthorizationSnapshot,
        LocationAuthorizationParseError
    > {
        .success(
            LocationAuthorizationSnapshot(
                authorization: .authorized(.whenInUse),
                accuracy: .full
            )
        )
    }

    @Test
    func startsBeforeFirstExecution() throws {
        let decision = try decideRangerStart(
            (
                phase: .notStarted,
                isAvailable: true,
                authorization: authorization
            )
        ).get()

        #expect(decision == .start)
    }

    @Test
    func keepsRunningWithoutRecheckingStartConditions() throws {
        let decision = try decideRangerStart(
            (
                phase: .running,
                isAvailable: false,
                authorization: .failure(.unsupportedAuthorizationStatus)
            )
        ).get()

        #expect(decision == .keepRunning)
    }

    @Test
    func rejectsRestartAfterTermination() {
        let result = decideRangerStart(
            (
                phase: .terminated,
                isAvailable: true,
                authorization: authorization
            )
        )

        switch result {
        case .failure(.executionTerminated):
            break
        default:
            Issue.record("Expected executionTerminated")
        }
    }
    
    @Test
    func rejectsUnavailableRanging() {
        let result = decideRangerStart(
            (
                phase: .notStarted,
                isAvailable: false,
                authorization: authorization
            )
        )

        switch result {
        case .failure(.unavailable):
            break
        default:
            Issue.record("Expected unavailable")
        }
    }
    
    @Test
    func rejectsDeniedAuthorizationBeforeStarting() {
        let result = decideRangerStart(
            (
                phase: .notStarted,
                isAvailable: true,
                authorization: .success(
                    LocationAuthorizationSnapshot(
                        authorization: .blocked(.denied),
                        accuracy: .full
                    )
                )
            )
        )

        switch result {
        case .failure(.authorizationRequired):
            break
        default:
            Issue.record("Expected authorizationRequired")
        }
    }

    @Test
    func rejectsReducedAccuracyBeforeStarting() {
        let result = decideRangerStart(
            (
                phase: .notStarted,
                isAvailable: true,
                authorization: .success(
                    LocationAuthorizationSnapshot(
                        authorization: .authorized(.whenInUse),
                        accuracy: .reduced
                    )
                )
            )
        )

        switch result {
        case .failure(.fullAccuracyRequired):
            break
        default:
            Issue.record("Expected fullAccuracyRequired")
        }
    }

    @Test(arguments: [
        LocationAuthorizationParseError.unsupportedAuthorizationStatus,
        .unsupportedAccuracyAuthorization,
    ])
    func preservesAuthorizationParsingFailure(
        _ error: LocationAuthorizationParseError
    ) {
        let result = decideRangerStart(
            (
                phase: .notStarted,
                isAvailable: true,
                authorization: .failure(error)
            )
        )

        switch (error, result) {
        case (
            .unsupportedAuthorizationStatus,
            .failure(.authorizationParsingFailed(.unsupportedAuthorizationStatus))
        ),
        (
            .unsupportedAccuracyAuthorization,
            .failure(.authorizationParsingFailed(.unsupportedAccuracyAuthorization))
        ):
            break
        default:
            Issue.record("Expected the original authorization parsing error")
        }
    }
    
    
}
