package io.buttersoft.feja.beacon

sealed interface BeaconDistanceEstimateParseError {
    data object NonFiniteValue : BeaconDistanceEstimateParseError
    data object NegativeValue : BeaconDistanceEstimateParseError
}