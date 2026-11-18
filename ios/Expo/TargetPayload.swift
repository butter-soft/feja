import Foundation

func makeBeaconTargetPayload(_ target: BeaconTarget) -> [String: Any] {
    switch target {
    case .uuid(let uuid):
        return [
            "uuid": uuid.uuidString
        ]
    case .uuidMajor(let uuid, let major):
        return ["uuid": uuid.uuidString, "major": Int(major)]
    case .uuidMajorMinor(let uuid, let major, let minor):
        return [
            "uuid": uuid.uuidString, "major": Int(major), "minor": Int(minor),
        ]
    }
}
