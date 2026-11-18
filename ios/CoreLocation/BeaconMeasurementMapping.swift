import CoreLocation

func makeBeaconMeasurement(from beacon: CLBeacon) -> BeaconMeasurement {
    BeaconMeasurement(
        uuid: beacon.uuid,
        major: beacon.major.uint16Value,
        minor: beacon.minor.uint16Value,
        proximity: parseBeaconProximity(beacon.proximity),
        rssi: beacon.rssi,
        accuracy: parseBeaconAccuracy(beacon.accuracy),
        observedAt: beacon.timestamp
    )
}
