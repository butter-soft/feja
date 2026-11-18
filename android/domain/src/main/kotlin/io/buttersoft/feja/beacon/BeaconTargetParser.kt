package io.buttersoft.feja.beacon

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import arrow.core.raise.ensure
import java.util.UUID
import kotlin.math.floor

internal fun parseBeaconIdentityValue(input: Double): Either<BeaconTargetParseError, UShort> =
    either {
        ensure(input.isFinite()) {
            BeaconTargetParseError.InvalidIdentityValue
        }

        ensure(input in 0.0..65535.0) {
            BeaconTargetParseError.InvalidIdentityValue
        }

        ensure(input == floor(input)) {
            BeaconTargetParseError.InvalidIdentityValue
        }

        input.toInt().toUShort()
    }

internal fun parseOptionalBeaconIdentityValue(input: Double?): Either<BeaconTargetParseError, UShort?> =
    either {
        input?.let { parseBeaconIdentityValue(it).bind() }
    }

internal fun parseBeaconUuid(input: String): Either<BeaconTargetParseError, UUID> =
    Either.catchOrThrow<IllegalArgumentException, UUID> { UUID.fromString(input) }.mapLeft {
        BeaconTargetParseError.InvalidUuid
    }.flatMap { uuid ->
        either {
            ensure(uuid.toString().equals(input, ignoreCase = true)) {
                BeaconTargetParseError.InvalidUuid
            }

            uuid
        }
    }

internal fun parseBeaconTargetIdentity(
    uuid: UUID,
    major: UShort?,
    minor: UShort?
): Either<BeaconTargetParseError, BeaconTarget> = either {
    ensure(major != null || minor == null) {
        BeaconTargetParseError.MinorWithoutMajor
    }

    when {
        major == null -> BeaconTarget.Uuid(uuid)
        minor == null -> BeaconTarget.UuidMajor(uuid, major)
        else -> BeaconTarget.UuidMajorMinor(uuid, major, minor)
    }
}

fun parseBeaconTargetInput(input: BeaconTargetInput): Either<BeaconTargetParseError, BeaconTarget> =
    either {
        val uuid = parseBeaconUuid(input.uuid).bind()
        val major = parseOptionalBeaconIdentityValue(input.major).bind()
        val minor = parseOptionalBeaconIdentityValue(input.minor).bind()

        parseBeaconTargetIdentity(uuid, major, minor).bind()
    }