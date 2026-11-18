package io.buttersoft.feja.altbeacon

import android.app.Notification
import org.altbeacon.beacon.Settings

// Module-owned notification ID; unrelated to beacon identity.
private const val SCAN_NOTIFICATION_ID = 17001

private const val SCAN_PERIOD_MS = 1100L

internal fun makeForegroundRangingSettings(
    notification: Notification,
): Settings =
    Settings(
        scanStrategy = Settings.ForegroundServiceScanStrategy(
            notification = notification,
            notificationId = SCAN_NOTIFICATION_ID,
        ),
        scanPeriods = Settings.ScanPeriods(
            foregroundScanPeriodMillis = SCAN_PERIOD_MS,
            foregroundBetweenScanPeriodMillis = 0L,
            backgroundScanPeriodMillis = SCAN_PERIOD_MS,
            backgroundBetweenScanPeriodMillis = 0L,
        ),
        longScanForcingEnabled = true,
    )