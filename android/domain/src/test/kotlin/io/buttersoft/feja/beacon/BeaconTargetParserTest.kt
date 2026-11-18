package io.buttersoft.feja.beacon

import arrow.core.Either
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class BeaconTargetParserTest : StringSpec({
    "실측 비콘 UUID만 지정하면 UUID 대상으로 파싱된다" {
        val uuidText = "706E7568-5F70-656F-706C-655F74610032"
        val input = BeaconTargetInput(
            uuid = uuidText,
            major = null,
            minor = null,
        )

        parseBeaconTargetInput(input) shouldBe
                Either.Right(BeaconTarget.Uuid(UUID.fromString(uuidText)))
    }

    "식별값의 최솟값과 최댓값을 허용한다" {
        parseBeaconIdentityValue(0.0) shouldBe
                Either.Right(0.toUShort())

        parseBeaconIdentityValue(65535.0) shouldBe
                Either.Right(65535.toUShort())
    }

    "범위 밖 값과 소수 및 유한하지 않은 값을 거부한다" {
        val invalidInputs = listOf(
            -1.0,
            65536.0,
            1.5,
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,
        )

        invalidInputs.forEach { input ->
            parseBeaconIdentityValue(input) shouldBe
                    Either.Left(BeaconTargetParseError.InvalidIdentityValue)
        }
    }

    "major만 지정하면 UUID와 major 대상으로 파싱된다" {
        val uuid = UUID.fromString("706E7568-5F70-656F-706C-655F74610032")

        parseBeaconTargetInput(
            BeaconTargetInput(uuid.toString(), 0.0, null)
        ) shouldBe Either.Right(
            BeaconTarget.UuidMajor(uuid, 0.toUShort())
        )
    }

    "major와 minor를 지정하면 세 식별값을 가진 대상으로 파싱된다" {
        val uuid = UUID.fromString("706E7568-5F70-656F-706C-655F74610032")

        parseBeaconTargetInput(
            BeaconTargetInput(uuid.toString(), 65535.0, 0.0)
        ) shouldBe Either.Right(
            BeaconTarget.UuidMajorMinor(
                uuid,
                65535.toUShort(),
                0.toUShort(),
            )
        )
    }

    "minor만 지정하면 오류를 반환한다" {
        parseBeaconTargetInput(
            BeaconTargetInput(
                uuid = "706E7568-5F70-656F-706C-655F74610032",
                major = null,
                minor = 0.0,
            )
        ) shouldBe Either.Left(BeaconTargetParseError.MinorWithoutMajor)
    }
})