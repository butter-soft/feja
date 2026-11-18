package io.buttersoft.feja.beacon

import arrow.core.Either
import java.util.UUID

data class BeaconMeasurement(
    val uuid: UUID,
    val major: UShort,
    val minor: UShort,
    val proximity: Either<BeaconProximityParseError, BeaconProximity>,
    val rssi: Int,
    val accuracy: Either<BeaconAccuracyParseError, BeaconAccuracy>,
    val observedAtMs: Long
)