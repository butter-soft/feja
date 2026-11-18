package io.buttersoft.feja.expo

import arrow.core.Either
import io.buttersoft.feja.beacon.BeaconAccuracy
import io.buttersoft.feja.beacon.BeaconAccuracyParseError
import io.buttersoft.feja.beacon.BeaconMeasurement
import io.buttersoft.feja.beacon.BeaconProximity
import io.buttersoft.feja.beacon.BeaconProximityParseError

internal fun makeBeaconAccuracyPayload(accuracy: Either<BeaconAccuracyParseError, BeaconAccuracy>): Map<String, Any> =
    accuracy.fold(
        ifLeft = { error ->
            when (error) {
                BeaconAccuracyParseError.NonFiniteValue -> mapOf(
                    "status" to "invalid",
                    "reason" to "nonFiniteValue"
                )
            }
        },
        ifRight = { value ->
            when (value) {
                is BeaconAccuracy.Estimated ->
                    mapOf(
                        "status" to "estimated",
                        "meters" to value.distance.meters,
                    )

                BeaconAccuracy.Undetermined ->
                    mapOf(
                        "status" to "undetermined",
                    )
            }
        }
    )

internal fun makeBeaconProximityPayload(proximity: Either<BeaconProximityParseError, BeaconProximity>): Map<String, Any> =
    proximity.fold(
        ifLeft = { error ->
            when (error) {
                is BeaconProximityParseError.UnsupportedValue -> mapOf(
                    "status" to "invalid",
                    "reason" to "unsupportedValue",
                    "rawValue" to error.rawValue
                )

                BeaconProximityParseError.NonFiniteDistance -> mapOf(
                    "status" to "invalid",
                    "reason" to "nonFiniteDistance"
                )
            }
        },
        ifRight = { value ->
            when (value) {
                BeaconProximity.UNKNOWN -> mapOf(
                    "status" to "undetermined"
                )

                BeaconProximity.IMMEDIATE -> mapOf(
                    "status" to "determined", "value" to "immediate"
                )

                BeaconProximity.NEAR -> mapOf(
                    "status" to "determined", "value" to "near"
                )

                BeaconProximity.FAR -> mapOf(
                    "status" to "determined", "value" to "far"
                )

            }
        }
    )

internal fun makeBeaconMeasurementPayload(measurement: BeaconMeasurement): Map<String, Any> = mapOf(
    "uuid" to measurement.uuid.toString().uppercase(),
    "major" to measurement.major.toInt(),
    "minor" to measurement.minor.toInt(),
    "rssi" to measurement.rssi,
    "proximity" to makeBeaconProximityPayload(measurement.proximity),
    "accuracy" to makeBeaconAccuracyPayload(measurement.accuracy),
    "observedAtMs" to measurement.observedAtMs,
)