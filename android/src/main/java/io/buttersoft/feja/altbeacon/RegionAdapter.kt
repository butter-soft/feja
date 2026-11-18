package io.buttersoft.feja.altbeacon

import io.buttersoft.feja.beacon.BeaconTarget
import io.buttersoft.feja.ranging.BeaconRangingExecutionId
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Identifier
import org.altbeacon.beacon.Region

internal fun makeRangingRegion(
    target: BeaconTarget,
    executionId: BeaconRangingExecutionId,
    parser: BeaconParser
): Region {
    val regionId = "expo-ibeacon:${executionId.rawValue}"
    val uuid = Identifier.fromUuid(target.uuid)

    return when (target) {
        is BeaconTarget.Uuid -> Region(regionId, parser, uuid, null, null)
        is BeaconTarget.UuidMajor -> Region(
            regionId,
            parser,
            uuid,
            Identifier.fromInt(target.major.toInt()),
            null
        )

        is BeaconTarget.UuidMajorMinor -> Region(
            regionId,
            parser,
            uuid,
            Identifier.fromInt(target.major.toInt()),
            Identifier.fromInt(target.minor.toInt())
        )
    }
}