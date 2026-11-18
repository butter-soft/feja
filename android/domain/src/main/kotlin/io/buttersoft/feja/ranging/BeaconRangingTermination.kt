package io.buttersoft.feja.ranging

import io.buttersoft.feja.beacon.BeaconFailure

sealed interface BeaconRangingTermination {
    data object Stopped : BeaconRangingTermination
    data class Failed(val error: BeaconFailure) : BeaconRangingTermination
}