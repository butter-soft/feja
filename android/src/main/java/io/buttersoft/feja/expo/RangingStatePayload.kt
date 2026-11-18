package io.buttersoft.feja.expo

import io.buttersoft.feja.ranging.BeaconRangingState

internal fun makeBeaconRangingStatePayload(state: BeaconRangingState): Map<String, Any> =
    when (state) {
        BeaconRangingState.Stopped -> mapOf(
            "status" to "stopped"
        )

        is BeaconRangingState.Running -> mapOf(
            "status" to "running",
            "target" to makeBeaconTargetPayload(state.target),
            "executionId" to state.executionId.rawValue.toString().uppercase()
        )
    }