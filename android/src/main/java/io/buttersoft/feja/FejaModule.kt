package io.buttersoft.feja

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import arrow.core.Either
import arrow.core.flatMap
import expo.modules.kotlin.exception.Exceptions
import expo.modules.kotlin.functions.Queues
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import io.buttersoft.feja.expo.BeaconCommandError
import io.buttersoft.feja.expo.BeaconTargetRecord
import io.buttersoft.feja.expo.makeBeaconCommandResultPayload
import io.buttersoft.feja.expo.makeBeaconRangingMeasurementParseErrorPayload
import io.buttersoft.feja.expo.makeBeaconRangingReceivedPayload
import io.buttersoft.feja.expo.makeBeaconRangingSnapshotPayload
import io.buttersoft.feja.expo.makeBeaconRangingStartedPayload
import io.buttersoft.feja.expo.makeBeaconRangingTerminatedPayload
import io.buttersoft.feja.expo.parseBeaconTargetRecord
import io.buttersoft.feja.ranging.BeaconLibraryFailure
import io.buttersoft.feja.ranging.BeaconRangingManager
import io.buttersoft.feja.ranging.BeaconRangingState
import org.altbeacon.beacon.BeaconManager

class FejaModule : Module() {
  private val mainHandler = Handler(Looper.getMainLooper())

  private val managerDelegate = lazy {
    val context =
      appContext.reactContext?.applicationContext ?: throw Exceptions.ReactContextLost()

    BeaconRangingManager(
      context = context,
      beaconManager = BeaconManager.getInstanceForApplication(context)
    )
  }

  @get:MainThread
  private val manager by managerDelegate

  @MainThread
  private fun releaseManager(): Either<BeaconLibraryFailure, Unit> {
    if (!managerDelegate.isInitialized()) {
      return Either.Right(Unit)
    }

    val currentManager = manager
    currentManager.onStarted = null
    currentManager.onReceived = null
    currentManager.onTerminated = null
    currentManager.onMeasurementParseError = null

    return when (val state = currentManager.state) {
      BeaconRangingState.Stopped -> Either.Right(Unit)
      is BeaconRangingState.Running -> currentManager.stop(state.target)
    }
  }

  @MainThread
  private fun connectEvents() {
    manager.onStarted = { revision, executionId, target, startedAtMs ->
      sendEvent(
        "onRangingStarted",
        makeBeaconRangingStartedPayload(
          revision = revision,
          target = target,
          executionId = executionId,
          startedAtMs = startedAtMs,
        ),
      )
    }

    manager.onReceived = { executionId, target, batch ->
      sendEvent(
        "onRangingReceived",
        makeBeaconRangingReceivedPayload(target, executionId, batch)
      )
    }

    manager.onTerminated = { revision, executionId, target, reason ->
      val terminatedAtMs = System.currentTimeMillis()

      sendEvent(
        "onRangingTerminated",
        makeBeaconRangingTerminatedPayload(revision = revision, target = target, executionId = executionId, terminatedAtMs = terminatedAtMs, reason =  reason)
      )
    }

    manager.onMeasurementParseError = { executionId, target, error ->
      val reportedAtMs = System.currentTimeMillis()

      sendEvent(
        "onRangingMeasurementParseError",
        makeBeaconRangingMeasurementParseErrorPayload(
          target = target,
          executionId = executionId,
          reportedAtMs = reportedAtMs,
          error = error,
        ),
      )
    }
  }

  @MainThread
  private fun start(record: BeaconTargetRecord): Either<BeaconCommandError, Unit> =
    parseBeaconTargetRecord(record).mapLeft {
      BeaconCommandError.InvalidTarget(it)
    }.flatMap { target ->
      connectEvents()
      manager.start(target).mapLeft { BeaconCommandError.StartFailed(it) }
    }

  @MainThread
  private fun stop(record: BeaconTargetRecord): Either<BeaconCommandError, Unit> =
    parseBeaconTargetRecord(record).mapLeft {
      BeaconCommandError.InvalidTarget(it)
    }.flatMap { target ->
      manager.stop(target).mapLeft { BeaconCommandError.StopFailed(it) }
    }

  override fun definition() = ModuleDefinition {
    Name("Feja")
    Events(
      "onRangingStarted",
      "onRangingReceived",
      "onRangingTerminated",
      "onRangingMeasurementParseError",
    )

    AsyncFunction("start") { record: BeaconTargetRecord ->
      makeBeaconCommandResultPayload(start(record))
    }.runOnQueue(Queues.MAIN)

    AsyncFunction("stop") { record: BeaconTargetRecord ->
      makeBeaconCommandResultPayload(stop(record))
    }.runOnQueue(Queues.MAIN)

    AsyncFunction("getSnapshot") {
      makeBeaconRangingSnapshotPayload(manager.snapshot)
    }.runOnQueue(Queues.MAIN)

    OnDestroy {
      mainHandler.post {
        releaseManager().onLeft { failure ->
          Log.e(
            "Feja",
            "Ranging cleanup failed: ${failure.exceptionType}: ${failure.message}",
          )
        }
      }
    }
  }
}
