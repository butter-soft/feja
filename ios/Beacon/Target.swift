import Foundation

enum BeaconTarget: Equatable {
    case uuid(UUID)
    case uuidMajor(uuid: UUID, major: UInt16)
    case uuidMajorMinor(uuid: UUID, major: UInt16, minor: UInt16)
}
