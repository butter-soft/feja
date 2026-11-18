package io.buttersoft.feja.beacon

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure

@JvmInline
value class BeaconDistanceEstimate private constructor(val meters: Double) {
    companion object {
        fun parse(input: Double): Either<BeaconDistanceEstimateParseError, BeaconDistanceEstimate> =
            either {
                ensure(input.isFinite()) {
                    BeaconDistanceEstimateParseError.NonFiniteValue
                }

                ensure(input >= 0.0) {
                    BeaconDistanceEstimateParseError.NegativeValue
                }

                BeaconDistanceEstimate(input)
            }
    }
}