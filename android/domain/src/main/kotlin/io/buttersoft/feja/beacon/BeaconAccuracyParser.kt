package io.buttersoft.feja.beacon

import arrow.core.Either

fun parseBeaconAccuracy(input: Double): Either<BeaconAccuracyParseError, BeaconAccuracy> =
    BeaconDistanceEstimate.parse(input).fold(
        ifLeft = { error ->
            when (error) {
                BeaconDistanceEstimateParseError.NegativeValue -> Either.Right(BeaconAccuracy.Undetermined)
                BeaconDistanceEstimateParseError.NonFiniteValue -> Either.Left(
                    BeaconAccuracyParseError.NonFiniteValue
                )
            }
        },
        ifRight = { distance ->
            Either.Right(BeaconAccuracy.Estimated(distance))
        }
    )