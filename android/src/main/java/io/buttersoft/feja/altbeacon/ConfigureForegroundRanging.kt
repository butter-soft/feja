package io.buttersoft.feja.altbeacon

import android.content.Context
import androidx.annotation.MainThread
import arrow.core.Either
import arrow.core.flatMap
import io.buttersoft.feja.ranging.BeaconLibraryFailure
import org.altbeacon.beacon.BeaconManager

@MainThread
internal fun configureForegroundRanging(
    context: Context,
    beaconManager: BeaconManager,
): Either<BeaconLibraryFailure, Unit> =
    createBeaconScanNotification(context).flatMap { notification ->
        Either.catch {
            beaconManager.adjustSettings(
                makeForegroundRangingSettings(notification),
            )
        }.mapLeft { error ->
            BeaconLibraryFailure(
                exceptionType = error.javaClass.name,
                message = error.message,
            )
        }
    }