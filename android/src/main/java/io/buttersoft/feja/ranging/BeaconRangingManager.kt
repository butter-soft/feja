package io.buttersoft.feja.ranging

import android.content.Context
import androidx.annotation.MainThread
import arrow.core.Either
import arrow.core.flatMap
import io.buttersoft.feja.altbeacon.BeaconRanger
import io.buttersoft.feja.altbeacon.IBeaconMeasurementParseError
import io.buttersoft.feja.beacon.BeaconTarget
import org.altbeacon.beacon.BeaconManager
import java.util.UUID

@MainThread
internal class BeaconRangingManager(context: Context, private val beaconManager: BeaconManager) {
    private companion object {
        val maxJavaScriptSafeInteger: ULong = (1uL shl 53) - 1uL
    }
    private val applicationContext = context.applicationContext

    private var activeRanger: BeaconRanger? = null
    private var revision: ULong = 0uL

    val state: BeaconRangingState
        get() = activeRanger?.takeIf { it.isRanging }?.let { ranger ->
            BeaconRangingState.Running(target = ranger.target, executionId = ranger.executionId)
        } ?: BeaconRangingState.Stopped
    val snapshot: BeaconRangingSnapshot get() = BeaconRangingSnapshot(revision = revision, state = state)

    var onStarted: ((ULong, BeaconRangingExecutionId, BeaconTarget, Long) -> Unit)? = null
    var onReceived:
            ((BeaconRangingExecutionId, BeaconTarget, RangingBatch) -> Unit)? = null
    var onTerminated:
            ((ULong, BeaconRangingExecutionId, BeaconTarget, BeaconRangingTermination) -> Unit)? = null
    var onMeasurementParseError:
            ((BeaconRangingExecutionId, BeaconTarget, IBeaconMeasurementParseError) -> Unit)? = null

    private fun advanceRevision() {
        check(revision < maxJavaScriptSafeInteger) {
            "Ranging revision exceeded JavaScript's safe integer range."
        }

        revision += 1uL
    }

    private fun connect(ranger: BeaconRanger) {
        val target = ranger.target

        ranger.onReceived = { executionId, batch ->
            if (activeRanger?.executionId == executionId) {
                onReceived?.invoke(executionId, target, batch)
            }
        }

        ranger.onTerminated = { executionId, reason ->
            if (activeRanger?.executionId == executionId) {
                activeRanger = null
                advanceRevision()
                onTerminated?.invoke(revision, executionId, target, reason)
            }
        }

        ranger.onMeasurementParseError = { executionId, error ->
            if (activeRanger?.executionId == executionId) {
                onMeasurementParseError?.invoke(executionId, target, error)
            }
        }
    }

    private fun startNewExecution(target: BeaconTarget): Either<BeaconRangingStartError, Unit> {
        val ranger = BeaconRanger(
            executionId = BeaconRangingExecutionId(UUID.randomUUID()),
            target,
            beaconManager,
            context = applicationContext
        )

        connect(ranger)

        return ranger.start().onRight {
            activeRanger = ranger
            advanceRevision()
            onStarted?.invoke(
                revision,
                ranger.executionId,
                target,
                System.currentTimeMillis(),
            )
        }
    }

    fun start(target: BeaconTarget): Either<BeaconRangingStartError, Unit> =
        decideBeaconRangingStart(state, target).flatMap { decision ->
            when (decision) {
                BeaconRangingStartDecision.START -> startNewExecution(target)
                BeaconRangingStartDecision.KEEP_RUNNING -> Either.Right(Unit)
            }
        }

    fun stop(target: BeaconTarget): Either<BeaconLibraryFailure, Unit> =
        activeRanger?.takeIf { it.target == target }?.stop() ?: Either.Right(
            Unit
        )
}