package io.buttersoft.feja.altbeacon

import arrow.core.Either
import io.buttersoft.feja.beacon.BeaconAccuracy
import io.buttersoft.feja.beacon.BeaconAccuracyParseError
import io.buttersoft.feja.beacon.BeaconDistanceEstimate
import io.buttersoft.feja.beacon.BeaconProximity
import io.buttersoft.feja.beacon.BeaconProximityParseError

private const val IMMEDIATE_LIMIT_METERS = 1.0
private const val NEAR_LIMIT_METERS = 3.0

/**
 * Android classification policy based on estimated distance.
 *
 * Reference: Zafari et al.,
 * "An iBeacon based Proximity and Indoor Localization System",
 * arXiv:1703.07876v2, Section II-B, Table I, p. 3.
 * https://arxiv.org/abs/1703.07876v2
 *
 * Immediate < 1 m, near 1–3 m inclusive, far > 3 m.
 * Does not reproduce Core Location's proximity algorithm.
 */
internal fun estimateBeaconProximity(distance: BeaconDistanceEstimate): BeaconProximity = when {
    distance.meters < IMMEDIATE_LIMIT_METERS -> BeaconProximity.IMMEDIATE
    distance.meters <= NEAR_LIMIT_METERS -> BeaconProximity.NEAR
    else -> BeaconProximity.FAR
}

fun deriveBeaconProximity(accuracy: Either<BeaconAccuracyParseError, BeaconAccuracy>): Either<BeaconProximityParseError, BeaconProximity> =
    accuracy.mapLeft { error ->
        when (error) {
            BeaconAccuracyParseError.NonFiniteValue -> BeaconProximityParseError.NonFiniteDistance
        }
    }.map { value ->
        when (value) {
            is BeaconAccuracy.Estimated -> estimateBeaconProximity(value.distance)

            BeaconAccuracy.Undetermined -> BeaconProximity.UNKNOWN
        }
    }