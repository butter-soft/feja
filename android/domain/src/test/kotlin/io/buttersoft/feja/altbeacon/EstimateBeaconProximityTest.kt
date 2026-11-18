package io.buttersoft.feja.altbeacon

import arrow.core.Either
import io.buttersoft.feja.beacon.BeaconProximity
import io.buttersoft.feja.beacon.BeaconProximityParseError
import io.buttersoft.feja.beacon.parseBeaconAccuracy
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EstimateBeaconProximityTest : StringSpec({
    "추정 거리를 정해진 경계에 따라 분류한다" {
        val cases = listOf(
            0.0 to BeaconProximity.IMMEDIATE,
            0.999 to BeaconProximity.IMMEDIATE,
            1.0 to BeaconProximity.NEAR,
            3.0 to BeaconProximity.NEAR,
            3.001 to BeaconProximity.FAR,
        )

        cases.forEach { (meters, expected) ->
            deriveBeaconProximity(parseBeaconAccuracy(meters)) shouldBe
                    Either.Right(expected)
        }
    }

    "거리 결정 불가는 근접도 결정 불가로 유지된다" {
        deriveBeaconProximity(parseBeaconAccuracy(-1.0)) shouldBe
                Either.Right(BeaconProximity.UNKNOWN)
    }

    "유한하지 않은 거리는 근접도 오류로 전달된다" {
        listOf(
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
        ).forEach { input ->
            deriveBeaconProximity(parseBeaconAccuracy(input)) shouldBe
                    Either.Left(BeaconProximityParseError.NonFiniteDistance)
        }
    }
})