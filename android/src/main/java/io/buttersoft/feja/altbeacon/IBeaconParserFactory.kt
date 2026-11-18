package io.buttersoft.feja.altbeacon

import org.altbeacon.beacon.BeaconParser

/**
 * iBeacon layout based on Apple's Proximity Beacon Specification,
 * Release R1 (2015-09-04), Section 2.1, Table 2-1, pp. 6–7.
 *
 * Specification available under "Artwork and specifications":
 * https://developer.apple.com/ibeacon/
 *
 * Layout offsets start at Company ID, byte 5 in the specification:
 * - 2–3: beacon type 0x0215
 * - 4–19: proximity UUID
 * - 20–21: major
 * - 22–23: minor
 * - 24: measured power
 */
private const val IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"

private const val IBEACON_PARSER_ID = "expo-beacon:ibeacon"

internal fun makeIBeaconParser(): BeaconParser =
    BeaconParser(IBEACON_PARSER_ID).setBeaconLayout(IBEACON_LAYOUT)