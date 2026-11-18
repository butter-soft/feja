enum BeaconCommandError: Error {
    case invalidTarget(BeaconTargetParseError)
    case startFailed(BeaconRangingStartError)
}
