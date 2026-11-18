package io.buttersoft.feja.beacon

sealed interface BeaconAccuracy {
    data class Estimated(val distance: BeaconDistanceEstimate) : BeaconAccuracy
    data object Undetermined : BeaconAccuracy
}