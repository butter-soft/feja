package io.buttersoft.feja.beacon

data class BeaconFailure(
    val domain: String,
    val code: Int,
    val message: String
)