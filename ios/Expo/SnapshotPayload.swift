func makeBeaconRangingSnapshotPayload(
    _ snapshot: BeaconRangingSnapshot
) -> [String: Any] {
    [
        "revision": Double(snapshot.revision),
        "state": makeBeaconRangingStatePayload(snapshot.state),
    ]
}
