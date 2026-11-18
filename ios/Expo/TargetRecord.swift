import ExpoModulesCore

struct BeaconTargetRecord: Record {
    @Field(.required)
    var uuid: String = ""

    @Field
    var major: Double?

    @Field
    var minor: Double?
}

func parseBeaconTargetRecord(_ record: BeaconTargetRecord) -> Result<
    BeaconTarget, BeaconTargetParseError
> {
    BeaconTargetInput(
        uuid: record.uuid,
        major: record.major,
        minor: record.minor
    ) |> parseBeaconTargetInput
}
