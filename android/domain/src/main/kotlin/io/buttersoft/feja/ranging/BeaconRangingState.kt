package io.buttersoft.feja.ranging

import io.buttersoft.feja.beacon.BeaconTarget

sealed interface BeaconRangingState {
    data object Stopped : BeaconRangingState
    data class Running(val target: BeaconTarget, val executionId: BeaconRangingExecutionId) :
        BeaconRangingState
}