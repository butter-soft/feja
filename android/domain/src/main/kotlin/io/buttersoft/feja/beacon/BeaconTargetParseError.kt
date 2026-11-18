package io.buttersoft.feja.beacon

sealed interface BeaconTargetParseError {
    data object InvalidUuid : BeaconTargetParseError
    data object InvalidIdentityValue : BeaconTargetParseError
    data object MinorWithoutMajor : BeaconTargetParseError
}