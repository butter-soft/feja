package io.buttersoft.feja.altbeacon

internal sealed interface IBeaconMeasurementParseError {
    data class InvalidIdentifierLayout(val byteCounts: List<Int>) : IBeaconMeasurementParseError
}