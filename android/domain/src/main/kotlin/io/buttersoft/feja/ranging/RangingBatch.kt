package io.buttersoft.feja.ranging

import io.buttersoft.feja.beacon.BeaconMeasurement

data class RangingBatch(val receivedAtMs: Long, val measurements: List<BeaconMeasurement>)