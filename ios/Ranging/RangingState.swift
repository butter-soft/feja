enum BeaconRangingState: Equatable {
    case stopped
    case running(target: BeaconTarget, executionID: BeaconRangingExecutionID)
}
