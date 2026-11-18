import CoreLocation

func makeRangingConstraint(from target: BeaconTarget)
    -> CLBeaconIdentityConstraint
{
    switch target {
    case .uuid(let uuid):
        return CLBeaconIdentityConstraint(uuid: uuid)
    case .uuidMajor(let uuid, let major):
        return CLBeaconIdentityConstraint(uuid: uuid, major: major)
    case .uuidMajorMinor(let uuid, let major, let minor):
        return CLBeaconIdentityConstraint(
            uuid: uuid,
            major: major,
            minor: minor
        )
    }
}
