package io.buttersoft.feja.altbeacon

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build

// Permission names are inlined strings; sdkInt selects the applicable set.
@SuppressLint("InlinedApi")
internal fun requiredRangingPermissions(sdkInt: Int): List<String> =
    if (sdkInt >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }