package io.buttersoft.feja.ranging

import io.buttersoft.feja.beacon.BeaconTarget

sealed interface BeaconRangingStartError {
    data class TargetConflict(val active: BeaconTarget, val requested: BeaconTarget) :
        BeaconRangingStartError

    data object ExecutionTerminated : BeaconRangingStartError
    data object AuthorizationRequired : BeaconRangingStartError
    data object Unavailable : BeaconRangingStartError
    data class LibraryFailure(val failure: BeaconLibraryFailure) :
        BeaconRangingStartError

    data class CleanupFailed(
        val startError: BeaconRangingStartError,
        val cleanupFailed: BeaconLibraryFailure
    ) : BeaconRangingStartError
}