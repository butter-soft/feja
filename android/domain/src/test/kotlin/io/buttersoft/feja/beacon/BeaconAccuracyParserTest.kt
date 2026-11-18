package io.buttersoft.feja.beacon

import arrow.core.Either
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class BeaconAccuracyParserTest : StringSpec({
    "0 이상의 유한한 거리는 추정값으로 보존된다" {
        listOf(0.0, 1.5, 100.0).forEach { meters ->
            val result = parseBeaconAccuracy(meters).map { accuracy ->
                when (accuracy) {
                    is BeaconAccuracy.Estimated -> accuracy.distance.meters
                    BeaconAccuracy.Undetermined -> null
                }
            }

            result shouldBe Either.Right(meters)
        }
    }

    "유한한 음수는 거리 결정 불가로 해석된다" {
        parseBeaconAccuracy(-1.0) shouldBe
                Either.Right(BeaconAccuracy.Undetermined)
    }

    "유한하지 않은 값은 파싱 오류로 처리된다" {
        listOf(
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
        ).forEach { input ->
            parseBeaconAccuracy(input) shouldBe
                    Either.Left(BeaconAccuracyParseError.NonFiniteValue)
        }
    }
})