import CoreLocation
import Foundation

@MainActor
final class BeaconRanger: NSObject, @MainActor CLLocationManagerDelegate {
    private let locationManager = CLLocationManager()
    private let constraint: CLBeaconIdentityConstraint

    private var phase: BeaconRangingExecutionPhase = .notStarted

    let executionID: BeaconRangingExecutionID
    let target: BeaconTarget

    var isRanging: Bool {
        if case .running = phase {
            return true
        }

        return false
    }

    var onReceived: ((BeaconRangingExecutionID, RangingBatch) -> Void)?
    var onTerminated:
        ((BeaconRangingExecutionID, BeaconRangingTermination) -> Void)?

    func locationManager(
        _ manager: CLLocationManager,
        didRange beacons: [CLBeacon],
        satisfying beaconConstraint: CLBeaconIdentityConstraint
    ) {
        let receivedAt = Date()

        guard isRanging else {
            return
        }

        let batch = RangingBatch(
            receivedAt: receivedAt,
            measurements: beacons.map(makeBeaconMeasurement)
        )

        onReceived?(executionID, batch)
    }

    func locationManager(
        _ manager: CLLocationManager,
        didFailRangingFor beaconConstraint: CLBeaconIdentityConstraint,
        error: any Error
    ) {
        guard isRanging else {
            return
        }

        let nsError = error as NSError

        let failure = BeaconFailure(
            domain: nsError.domain,
            code: nsError.code,
            message: nsError.localizedDescription
        )

        terminate(.failed(failure))
    }

    init(
        executionID: BeaconRangingExecutionID,
        target: BeaconTarget
    ) {
        self.executionID = executionID
        self.target = target
        self.constraint = makeRangingConstraint(from: target)

        super.init()

        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyThreeKilometers
        locationManager.distanceFilter = 3000
        locationManager.pausesLocationUpdatesAutomatically = false
        locationManager.allowsBackgroundLocationUpdates = true
    }

    private func terminate(_ reason: BeaconRangingTermination) {
        guard isRanging else {
            return
        }

        phase = .terminated

        locationManager.delegate = nil
        locationManager.stopRangingBeacons(satisfying: constraint)

        let notify = onTerminated
        onReceived = nil
        onTerminated = nil

        notify?(executionID, reason)
    }

    func stop() {
        terminate(.stopped)
    }

    private func executeStart(_ decision: BeaconRangingStartDecision) {
        switch decision {
        case .start:
            phase = .running
            locationManager.startRangingBeacons(satisfying: constraint)
        case .keepRunning:
            break
        }
    }

    func start() -> Result<Void, BeaconRangingStartError> {
        let authorization =
            (
                authorization: locationManager.authorizationStatus,
                accuracy: locationManager.accuracyAuthorization
            ) |> parseLocationAuthorizationSnapshot

        let input = (
            phase: phase, isAvailable: CLLocationManager.isRangingAvailable(),
            authorization: authorization
        )

        return input |> decideRangerStart |> map(executeStart)
    }
}
