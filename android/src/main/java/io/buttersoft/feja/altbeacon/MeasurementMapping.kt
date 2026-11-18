package io.buttersoft.feja.altbeacon

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.buttersoft.feja.beacon.BeaconAccuracy
import io.buttersoft.feja.beacon.BeaconAccuracyParseError
import io.buttersoft.feja.beacon.BeaconMeasurement
import io.buttersoft.feja.beacon.parseBeaconAccuracy
import org.altbeacon.beacon.Beacon

internal fun readBeaconAccuracy(beacon: Beacon): Either<BeaconAccuracyParseError, BeaconAccuracy> =
    parseBeaconAccuracy(beacon.distance)

internal fun parseIBeaconMeasurement(beacon: Beacon): Either<IBeaconMeasurementParseError, BeaconMeasurement> =
    either {
        val identifiers = beacon.identifiers.toList()
        val byteCounts = identifiers.map { it.byteCount }

        ensure(byteCounts == listOf(16, 2, 2)) {
            IBeaconMeasurementParseError.InvalidIdentifierLayout(byteCounts)
        }

        val accuracy = readBeaconAccuracy(beacon)

        BeaconMeasurement(
            uuid = identifiers[0].toUuid(),
            major = identifiers[1].toInt().toUShort(),
            minor = identifiers[2].toInt().toUShort(),
            proximity = deriveBeaconProximity(accuracy),
            rssi = beacon.rssi,
            accuracy = accuracy,
            observedAtMs = beacon.lastCycleDetectionTimestamp
        )
    }