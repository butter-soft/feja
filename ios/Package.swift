// swift-tools-version: 6.0

import PackageDescription

let package = Package(
    name: "FejaDomain",
    platforms: [.macOS(.v13)],
    products: [
        .library(
            name: "FejaDomain",
            targets: ["FejaDomain"]
        ),
    ],
    targets: [
        .target(
            name: "FejaDomain",
            path: ".",
            exclude: [
                "Feja.podspec",
                "FejaModule.swift",
                "CoreLocation",
                "Expo",
                "Ranging/Manager.swift",
                "Tests",
            ],
            sources: [
                "Beacon",
                "Functional",
                "LocationAuthorization",
                "Ranging",
            ]
        ),
        .testTarget(
            name: "FejaDomainTests",
            dependencies: ["FejaDomain"],
            path: "Tests/FejaDomainTests"
        ),
    ],
    swiftLanguageModes: [.v5]
)
