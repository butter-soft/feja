func makeBeaconCommandResultPayload(
    _ result: Result<Void, BeaconCommandError>
) -> [String: Any] {
    switch result {
    case .success:
        return [
            "status": "success"
        ]

    case .failure(let error):
        return [
            "status": "failure",
            "error": makeBeaconCommandErrorPayload(error)
        ]
    }
}
