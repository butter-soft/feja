package io.buttersoft.feja.altbeacon

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import io.buttersoft.feja.ranging.BeaconRangingStartError

internal fun readRangingAuthorization(context: Context): Either<BeaconRangingStartError, Unit> =
    either {
        val permissions = requiredRangingPermissions(Build.VERSION.SDK_INT)

        ensure(
            permissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        ) {
            BeaconRangingStartError.AuthorizationRequired
        }
    }