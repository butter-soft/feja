package io.buttersoft.feja.expo

import io.buttersoft.feja.beacon.BeaconTargetParseError
import io.buttersoft.feja.ranging.BeaconLibraryFailure
import io.buttersoft.feja.ranging.BeaconRangingStartError

internal sealed interface BeaconCommandError {
    data class InvalidTarget(
        val reason: BeaconTargetParseError,
    ) : BeaconCommandError

    data class StartFailed(
        val reason: BeaconRangingStartError,
    ) : BeaconCommandError

    data class StopFailed(
        val reason: BeaconLibraryFailure,
    ) : BeaconCommandError
}