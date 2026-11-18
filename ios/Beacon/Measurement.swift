import Foundation

struct BeaconMeasurement {
    let uuid: UUID
    let major: UInt16
    let minor: UInt16
    let proximity: Result<BeaconProximity, BeaconProximityParseError>
    let rssi: Int
    let accuracy: Result<BeaconAccuracy, BeaconAccuracyParseError>
    let observedAt: Date
}
