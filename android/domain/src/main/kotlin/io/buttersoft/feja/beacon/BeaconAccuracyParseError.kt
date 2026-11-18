package io.buttersoft.feja.beacon

sealed interface BeaconAccuracyParseError {
    data object NonFiniteValue : BeaconAccuracyParseError
}