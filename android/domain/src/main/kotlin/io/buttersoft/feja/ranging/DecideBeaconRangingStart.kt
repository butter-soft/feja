package io.buttersoft.feja.ranging

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.buttersoft.feja.beacon.BeaconTarget

fun decideBeaconRangingStart(
    state: BeaconRangingState,
    requestedTarget: BeaconTarget
): Either<BeaconRangingStartError, BeaconRangingStartDecision> =
    either {
        when (state) {
            BeaconRangingState.Stopped -> BeaconRangingStartDecision.START

            is BeaconRangingState.Running -> {
                ensure(state.target == requestedTarget) {
                    BeaconRangingStartError.TargetConflict(
                        active = state.target,
                        requested = requestedTarget
                    )
                }

                BeaconRangingStartDecision.KEEP_RUNNING
            }
        }
    }