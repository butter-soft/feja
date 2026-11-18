package io.buttersoft.feja.expo

import io.buttersoft.feja.beacon.BeaconTarget

internal fun makeBeaconTargetPayload(target: BeaconTarget): Map<String, Any> {
    val uuid = target.uuid.toString().uppercase()

    return when (target) {
        is BeaconTarget.Uuid -> mapOf("uuid" to uuid)
        is BeaconTarget.UuidMajor -> mapOf(
            "uuid" to uuid,
            "major" to target.major.toInt()
        )

        is BeaconTarget.UuidMajorMinor -> mapOf(
            "uuid" to uuid,
            "major" to target.major.toInt(),
            "minor" to target.minor.toInt()
        )
    }
}