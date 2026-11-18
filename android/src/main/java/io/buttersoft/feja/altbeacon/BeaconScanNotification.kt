package io.buttersoft.feja.altbeacon

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import arrow.core.Either
import io.buttersoft.feja.R
import io.buttersoft.feja.ranging.BeaconLibraryFailure

private const val SCAN_CHANNEL_ID = "expo-beacon:ranging"

internal fun createBeaconScanNotification(
    context: Context,
): Either<BeaconLibraryFailure, Notification> =
    Either.catch {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SCAN_CHANNEL_ID,
                "Beacon scanning",
                NotificationManager.IMPORTANCE_LOW,
            )

            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        val launchIntent =
            context.packageManager.getLaunchIntentForPackage(context.packageName)

        val contentIntent = launchIntent?.let { intent ->
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        NotificationCompat.Builder(context, SCAN_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_beacon_scan)
            .setContentTitle("Beacon scanning")
            .setContentText("Scanning for nearby beacons")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent)
            .build()
    }.mapLeft { error ->
        BeaconLibraryFailure(
            exceptionType = error.javaClass.name,
            message = error.message,
        )
    }