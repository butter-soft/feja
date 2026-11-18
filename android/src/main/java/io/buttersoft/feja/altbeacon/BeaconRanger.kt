package io.buttersoft.feja.altbeacon

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.recover
import io.buttersoft.feja.beacon.BeaconTarget
import io.buttersoft.feja.beacon.BeaconFailure
import io.buttersoft.feja.ranging.BeaconLibraryFailure
import io.buttersoft.feja.ranging.BeaconRangingExecutionId
import io.buttersoft.feja.ranging.BeaconRangingExecutionPhase
import io.buttersoft.feja.ranging.BeaconRangingStartDecision
import io.buttersoft.feja.ranging.BeaconRangingStartError
import io.buttersoft.feja.ranging.BeaconRangingTermination
import io.buttersoft.feja.ranging.RangingBatch
import io.buttersoft.feja.ranging.decideRangingExecutionStart
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.RangeNotifier

@MainThread
internal class BeaconRanger(
    val executionId: BeaconRangingExecutionId,
    val target: BeaconTarget,
    private val beaconManager: BeaconManager,
    context: Context
) {
    private val applicationContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val region = makeRangingRegion(target, executionId, makeIBeaconParser())

    private val rangeNotifier = RangeNotifier { beacons, receivedRegion ->
        val receivedAtMs = System.currentTimeMillis()

        if (receivedRegion.uniqueId != region.uniqueId) {
            return@RangeNotifier
        }

        val results = beacons.map(::parseIBeaconMeasurement)

        mainHandler.post {
            if (!isRanging) {
                return@post
            }

            val measurements = results.mapNotNull { result ->
                result.fold(
                    ifLeft = { error ->
                        if (isRanging) {
                            onMeasurementParseError?.invoke(executionId, error)
                        }
                        null
                    },
                    ifRight = { it }
                )
            }

            if (!isRanging) {
                return@post
            }

            onReceived?.invoke(executionId, RangingBatch(receivedAtMs, measurements))
        }
    }

    private var phase = BeaconRangingExecutionPhase.NOT_STARTED

    val isRanging: Boolean get() = phase == BeaconRangingExecutionPhase.RUNNING

    var onReceived: ((BeaconRangingExecutionId, RangingBatch) -> Unit)? = null
    var onTerminated: ((BeaconRangingExecutionId, BeaconRangingTermination) -> Unit)? = null
    var onMeasurementParseError: ((BeaconRangingExecutionId, IBeaconMeasurementParseError) -> Unit)? =
        null

    private fun rollbackStart(): Either<BeaconLibraryFailure, Unit> {
        phase = BeaconRangingExecutionPhase.TERMINATED

        onReceived = null
        onTerminated = null
        onMeasurementParseError = null

        beaconManager.removeRangeNotifier(rangeNotifier)

        return requestStop()
    }

    private fun prepareStart(): Either<BeaconRangingStartError, BeaconRangingStartDecision> =
        decideRangingExecutionStart(phase).flatMap { decision ->
            when (decision) {
                BeaconRangingStartDecision.START -> readRangingAuthorization(applicationContext).flatMap {
                    readBluetoothAvailability(applicationContext)
                }.map { decision }

                BeaconRangingStartDecision.KEEP_RUNNING -> Either.Right(decision)
            }
        }

    private fun requestStart(): Either<BeaconRangingStartError, Unit> = Either.catch {
        beaconManager.addRangeNotifier(rangeNotifier)
        beaconManager.startRangingBeacons(region)
    }.mapLeft { error ->
        when (error) {
            is SecurityException -> BeaconRangingStartError.AuthorizationRequired

            else -> BeaconRangingStartError.LibraryFailure(
                BeaconLibraryFailure(
                    exceptionType = error.javaClass.name,
                    message = error.message
                )
            )
        }
    }

    private fun requestStop(): Either<BeaconLibraryFailure, Unit> =
        Either.catch { beaconManager.stopRangingBeacons(region) }.mapLeft { error ->
            BeaconLibraryFailure(
                exceptionType = error.javaClass.name,
                message = error.message
            )
        }

    private fun terminate(reason: BeaconRangingTermination): Either<BeaconLibraryFailure, Unit> {
        if (!isRanging) {
            return Either.Right(Unit)
        }

        phase = BeaconRangingExecutionPhase.TERMINATED

        val notify = onTerminated
        onReceived = null
        onTerminated = null
        onMeasurementParseError = null

        beaconManager.removeRangeNotifier(rangeNotifier)

        val result = requestStop()
        val termination = result.fold(
            ifLeft = { failure ->
                BeaconRangingTermination.Failed(
                    BeaconFailure(
                        domain = failure.exceptionType,
                        code = 0,
                        message = failure.message ?: failure.exceptionType
                    )
                )

            },
            ifRight = { reason }
        )

        notify?.invoke(executionId, termination)

        return result
    }

    fun stop(): Either<BeaconLibraryFailure, Unit> = terminate(BeaconRangingTermination.Stopped)

    private fun startNewExecution(): Either<BeaconRangingStartError, Unit> =
        configureForegroundRanging(applicationContext, beaconManager)
            .mapLeft<BeaconRangingStartError> { failure ->
                BeaconRangingStartError.LibraryFailure(failure)
            }
            .flatMap {
                requestStart().recover { startError ->
                    rollbackStart().mapLeft { cleanupFailure ->
                        BeaconRangingStartError.CleanupFailed(
                            startError,
                            cleanupFailure,
                        )
                    }.bind()

                    raise(startError)
                }
            }
            .onRight {
                phase = BeaconRangingExecutionPhase.RUNNING
            }

    fun start(): Either<BeaconRangingStartError, Unit> = prepareStart().flatMap { decision ->
        when (decision) {
            BeaconRangingStartDecision.START -> startNewExecution()
            BeaconRangingStartDecision.KEEP_RUNNING -> Either.Right(Unit)
        }
    }
}