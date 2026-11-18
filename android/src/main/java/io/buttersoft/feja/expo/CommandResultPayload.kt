package io.buttersoft.feja.expo

import arrow.core.Either

internal fun makeBeaconCommandResultPayload(
    result: Either<BeaconCommandError, Unit>,
): Map<String, Any> =
    result.fold(
        ifLeft = { error ->
            mapOf(
                "status" to "failure",
                "error" to makeBeaconCommandErrorPayload(error),
            )
        },
        ifRight = {
            mapOf(
                "status" to "success",
            )
        },
    )