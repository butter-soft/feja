import Testing

@testable import FejaDomain

struct AuthorizedRangingStartTests {
    @Test(arguments: [
        LocationAuthorizationScope.whenInUse,
        .always,
    ])
    func acceptsAuthorizedScopeWithFullAccuracy(
        _ scope: LocationAuthorizationScope
    ) throws {
        let decision = try decideAuthorizedRangingStart(
            LocationAuthorizationSnapshot(
                authorization: .authorized(scope),
                accuracy: .full
            )
        ).get()

        #expect(decision == .start)
    }

    @Test
    func rejectsReducedAccuracy() {
        let result = decideAuthorizedRangingStart(
            LocationAuthorizationSnapshot(
                authorization: .authorized(.whenInUse),
                accuracy: .reduced
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
        LocationAuthorizationState.notDetermined,
        .blocked(.denied),
        .blocked(.restricted),
    ])
    func rejectsMissingAuthorization(
        _ authorization: LocationAuthorizationState
    ) {
        let result = decideAuthorizedRangingStart(
            LocationAuthorizationSnapshot(
                authorization: authorization,
                accuracy: .full
            )
        )

        switch result {
        case .failure(.authorizationRequired):
            break
        default:
            Issue.record("Expected authorizationRequired")
        }
    }
}
