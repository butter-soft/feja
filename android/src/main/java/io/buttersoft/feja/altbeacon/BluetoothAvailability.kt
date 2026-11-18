package io.buttersoft.feja.altbeacon

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.buttersoft.feja.ranging.BeaconRangingStartError

// SecurityException is converted to AuthorizationRequired below.
@SuppressLint("MissingPermission")
internal fun readBluetoothAvailability(context: Context): Either<BeaconRangingStartError, Unit> =
    either {
        val available = Either.catchOrThrow<SecurityException, Boolean> {
            val supportsBle =
                context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
            val manager = context.getSystemService(BluetoothManager::class.java)

            supportsBle && manager?.adapter?.isEnabled == true
        }.mapLeft {
            BeaconRangingStartError.AuthorizationRequired
        }.bind()

        ensure(available) {
            BeaconRangingStartError.Unavailable
        }
    }