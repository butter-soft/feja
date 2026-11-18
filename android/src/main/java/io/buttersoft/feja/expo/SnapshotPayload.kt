package io.buttersoft.feja.expo

import io.buttersoft.feja.ranging.BeaconRangingSnapshot

internal fun makeBeaconRangingSnapshotPayload(
    snapshot: BeaconRangingSnapshot,
): Map<String, Any> =
    mapOf(
        "revision" to snapshot.revision.toDouble(),
        "state" to makeBeaconRangingStatePayload(snapshot.state),
    )