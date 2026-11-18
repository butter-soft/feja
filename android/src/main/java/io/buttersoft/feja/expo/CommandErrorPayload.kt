package io.buttersoft.feja.expo

import io.buttersoft.feja.beacon.BeaconTargetParseError
import io.buttersoft.feja.ranging.BeaconLibraryFailure
import io.buttersoft.feja.ranging.BeaconRangingStartError

private fun makeErrorPayload(
    code: String,
    message: String,
    details: Map<String, Any?> = emptyMap(),
): Map<String, Any> =
    mapOf(
        "code" to code,
        "message" to message,
        "details" to details,
    )

private fun makeBeaconTargetParseErrorPayload(
    error: BeaconTargetParseError,
): Map<String, Any> =
    when (error) {
        BeaconTargetParseError.InvalidUuid ->
            makeErrorPayload(
                code = "ERR_IBEACON_INVALID_UUID",
                message = "UUID must be a valid UUID string.",
            )

        BeaconTargetParseError.InvalidIdentityValue ->
            makeErrorPayload(
                code = "ERR_IBEACON_INVALID_IDENTITY_VALUE",
                message = "Major and minor must be integers between 0 and 65535.",
            )

        BeaconTargetParseError.MinorWithoutMajor ->
            makeErrorPayload(
                code = "ERR_IBEACON_MINOR_WITHOUT_MAJOR",
                message = "Minor requires major.",
            )
    }

private fun makeBeaconLibraryFailurePayload(
    failure: BeaconLibraryFailure,
): Map<String, Any?> =
    mapOf(
        "exceptionType" to failure.exceptionType,
        "message" to failure.message,
    )

private fun makeBeaconRangingStartErrorPayload(
    error: BeaconRangingStartError,
): Map<String, Any> =
    when (error) {
        is BeaconRangingStartError.TargetConflict ->
            makeErrorPayload(
                code = "ERR_IBEACON_TARGET_CONFLICT",
                message = "Ranging is already running for a different target.",
                details = mapOf(
                    "active" to makeBeaconTargetPayload(error.active),
                    "requested" to makeBeaconTargetPayload(error.requested),
                ),
            )

        BeaconRangingStartError.ExecutionTerminated ->
            makeErrorPayload(
                code = "ERR_IBEACON_EXECUTION_TERMINATED",
                message = "A terminated ranging execution cannot be restarted.",
            )

        BeaconRangingStartError.AuthorizationRequired ->
            makeErrorPayload(
                code = "ERR_IBEACON_AUTHORIZATION_REQUIRED",
                message = "Required permissions have not been granted.",
            )

        BeaconRangingStartError.Unavailable ->
            makeErrorPayload(
                code = "ERR_IBEACON_RANGING_UNAVAILABLE",
                message = "Beacon ranging is unavailable.",
            )

        is BeaconRangingStartError.LibraryFailure ->
            makeErrorPayload(
                code = "ERR_IBEACON_RANGING_START_FAILED",
                message = "Failed to start beacon ranging.",
                details = makeBeaconLibraryFailurePayload(error.failure),
            )

        is BeaconRangingStartError.CleanupFailed ->
            makeErrorPayload(
                code = "ERR_IBEACON_RANGING_START_CLEANUP_FAILED",
                message = "Starting beacon ranging and cleaning up both failed.",
                details = mapOf(
                    "startError" to makeBeaconRangingStartErrorPayload(error.startError),
                    "cleanupFailure" to makeBeaconLibraryFailurePayload(error.cleanupFailed),
                ),
            )
    }

internal fun makeBeaconCommandErrorPayload(
    error: BeaconCommandError,
): Map<String, Any> =
    when (error) {
        is BeaconCommandError.InvalidTarget ->
            makeBeaconTargetParseErrorPayload(error.reason)

        is BeaconCommandError.StartFailed ->
            makeBeaconRangingStartErrorPayload(error.reason)

        is BeaconCommandError.StopFailed ->
            makeErrorPayload(
                code = "ERR_IBEACON_RANGING_STOP_FAILED",
                message = "Failed to stop beacon ranging.",
                details = makeBeaconLibraryFailurePayload(error.reason),
            )
    }