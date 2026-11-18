package io.buttersoft.feja.ranging

import arrow.core.Either
import arrow.core.raise.either

fun decideRangingExecutionStart(phase: BeaconRangingExecutionPhase): Either<BeaconRangingStartError, BeaconRangingStartDecision> =
    either {
        when (phase) {
            BeaconRangingExecutionPhase.NOT_STARTED -> BeaconRangingStartDecision.START
            BeaconRangingExecutionPhase.RUNNING -> BeaconRangingStartDecision.KEEP_RUNNING
            BeaconRangingExecutionPhase.TERMINATED -> raise(BeaconRangingStartError.ExecutionTerminated)
        }
    }