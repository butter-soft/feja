package io.buttersoft.feja.beacon

import java.util.UUID

sealed interface BeaconTarget {
    val uuid: UUID
    data class Uuid(override val uuid: UUID) : BeaconTarget
    data class UuidMajor(override val uuid: UUID, val major: UShort) : BeaconTarget
    data class UuidMajorMinor(override val uuid: UUID, val major: UShort, val minor: UShort) :
        BeaconTarget
}