import Foundation
import Testing

@testable import FejaDomain

struct TargetParserTests {
    private let uuidText = "706E7568-5F70-656F-706C-655F74610032"

    private var uuid: UUID {
        get throws {
            try #require(UUID(uuidString: uuidText))
        }
    }

    @Test(arguments: [0.0, 65535.0])
    func acceptsIdentityBoundaries(_ input: Double) throws {
        let value = try parseBeaconIdentityValue(input).get()

        #expect(Double(value) == input)
    }

    @Test(arguments: [
        -1.0,
        65536.0,
        1.5,
        Double.nan,
        Double.infinity,
        -Double.infinity,
    ])
    func rejectsInvalidIdentityValues(_ input: Double) {
        switch parseBeaconIdentityValue(input) {
        case .failure(.invalidIdentityValue):
            break
        default:
            Issue.record("Expected invalidIdentityValue for \(input)")
        }
    }

    @Test
    func acceptsObservedBeaconIdentifier() throws {
        let target = try parseBeaconTargetInput(
            BeaconTargetInput(uuid: uuidText, major: nil, minor: nil)
        ).get()

        #expect(target == .uuid(try uuid))
    }
    
    @Test
    func acceptsMajorWithoutMinor() throws {
        let target = try parseBeaconTargetInput(
            BeaconTargetInput(uuid: uuidText, major: 0, minor: nil)
        ).get()

        #expect(target == .uuidMajor(uuid: try uuid, major: 0))
    }

    @Test
    func acceptsMajorAndMinor() throws {
        let target = try parseBeaconTargetInput(
            BeaconTargetInput(uuid: uuidText, major: 65535, minor: 0)
        ).get()

        #expect(
            target == .uuidMajorMinor(uuid: try uuid, major: 65535, minor: 0)
        )
    }

    @Test
    func rejectsMinorWithoutMajor() {
        let input = BeaconTargetInput(
            uuid: uuidText,
            major: nil,
            minor: 0
        )

        switch parseBeaconTargetInput(input) {
        case .failure(.minorWithoutMajor):
            break
        default:
            Issue.record("Expected minorWithoutMajor")
        }
    }
}
