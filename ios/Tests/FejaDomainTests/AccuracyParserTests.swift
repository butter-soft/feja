import Testing

@testable import FejaDomain

struct AccuracyParserTests {
    @Test(arguments: [0.0, 1.5])
    func preservesEstimatedDistance(_ input: Double) throws {
        let accuracy = try parseBeaconAccuracy(input).get()

        switch accuracy {
        case .estimated(let distance):
            #expect(distance.meters == input)
        case .undetermined:
            Issue.record("Expected an estimated distance")
        }
    }

    @Test
    func treatsNegativeDistanceAsUndetermined() throws {
        let accuracy = try parseBeaconAccuracy(-1).get()

        switch accuracy {
        case .undetermined:
            break
        case .estimated:
            Issue.record("Expected undetermined accuracy")
        }
    }

    @Test(arguments: [
        Double.nan,
        Double.infinity,
        -Double.infinity,
    ])
    func rejectsNonFiniteDistance(_ input: Double) {
        switch parseBeaconAccuracy(input) {
        case .failure(.nonFiniteValue):
            break
        default:
            Issue.record("Expected nonFiniteValue for \(input)")
        }
    }
}
