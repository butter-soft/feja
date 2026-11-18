package io.buttersoft.feja.expo

import io.buttersoft.feja.beacon.BeaconTarget
import io.buttersoft.feja.altbeacon.IBeaconMeasurementParseError
import io.buttersoft.feja.ranging.BeaconRangingExecutionId
import io.buttersoft.feja.ranging.BeaconRangingTermination
import io.buttersoft.feja.ranging.RangingBatch

internal fun makeBeaconRangingStartedPayload(
    revision: ULong,
    target: BeaconTarget,
    executionId: BeaconRangingExecutionId,
    startedAtMs: Long,
): Map<String, Any> =
    mapOf(
        "revision" to revision.toDouble(),
        "target" to makeBeaconTargetPayload(target),
        "executionId" to executionId.rawValue.toString().uppercase(),
        "startedAtMs" to startedAtMs,
    )

internal fun makeBeaconRangingReceivedPayload(
    target: BeaconTarget,
    executionId: BeaconRangingExecutionId,
    batch: RangingBatch,
): Map<String, Any> =
    mapOf(
        "target" to makeBeaconTargetPayload(target),
        "executionId" to executionId.rawValue.toString().uppercase(),
        "receivedAtMs" to batch.receivedAtMs,
        "measurements" to batch.measurements.map(::makeBeaconMeasurementPayload),
    )

internal fun makeBeaconRangingTerminatedPayload(
    revision: ULong,
    target: BeaconTarget,
    executionId: BeaconRangingExecutionId,
    terminatedAtMs: Long,
    reason: BeaconRangingTermination,
): Map<String, Any> {
    val execution: Map<String, Any> = mapOf(
        "revision" to revision.toDouble(),
        "target" to makeBeaconTargetPayload(target),
        "executionId" to executionId.rawValue.toString().uppercase(),
        "terminatedAtMs" to terminatedAtMs,
    )

    val outcome: Map<String, Any> = when (reason) {
        BeaconRangingTermination.Stopped ->
            mapOf("reason" to "stopped")

        is BeaconRangingTermination.Failed ->
            mapOf(
                "reason" to "failed",
                "error" to mapOf(
                    "domain" to reason.error.domain,
                    "code" to reason.error.code,
                    "message" to reason.error.message,
                ),
            )
    }

    return execution + outcome
}

internal fun makeBeaconRangingMeasurementParseErrorPayload(
    target: BeaconTarget,
    executionId: BeaconRangingExecutionId,
    reportedAtMs: Long,
    error: IBeaconMeasurementParseError,
): Map<String, Any> {
    val errorPayload: Map<String, Any> = when (error) {
        is IBeaconMeasurementParseError.InvalidIdentifierLayout ->
            mapOf(
                "reason" to "invalidIdentifierLayout",
                "byteCounts" to error.byteCounts,
            )
    }

    return mapOf(
        "target" to makeBeaconTargetPayload(target),
        "executionId" to executionId.rawValue.toString().uppercase(),
        "reportedAtMs" to reportedAtMs,
        "error" to errorPayload,
    )
}