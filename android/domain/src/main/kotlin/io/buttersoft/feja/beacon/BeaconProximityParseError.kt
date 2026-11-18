package io.buttersoft.feja.beacon

sealed interface BeaconProximityParseError {
    data class UnsupportedValue(val rawValue: Int) : BeaconProximityParseError
    data object NonFiniteDistance : BeaconProximityParseError
}