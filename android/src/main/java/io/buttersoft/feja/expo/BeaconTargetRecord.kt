package io.buttersoft.feja.expo

import arrow.core.Either
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record
import expo.modules.kotlin.records.Required
import io.buttersoft.feja.beacon.BeaconTarget
import io.buttersoft.feja.beacon.BeaconTargetInput
import io.buttersoft.feja.beacon.BeaconTargetParseError
import io.buttersoft.feja.beacon.parseBeaconTargetInput

internal class BeaconTargetRecord : Record {
    @Field
    @Required
    val uuid: String = ""

    @Field
    val major: Double? = null

    @Field
    val minor: Double? = null
}

internal fun parseBeaconTargetRecord(record: BeaconTargetRecord): Either<BeaconTargetParseError, BeaconTarget> =
    parseBeaconTargetInput(
        BeaconTargetInput(uuid = record.uuid, major = record.major, minor = record.minor)
    )