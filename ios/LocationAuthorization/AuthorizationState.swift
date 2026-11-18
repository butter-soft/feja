enum LocationAuthorizationScope {
    case whenInUse
    case always
}

enum LocationAuthorizationBlockReason {
    case denied
    case restricted
}

enum LocationAuthorizationState {
    case notDetermined
    case blocked(LocationAuthorizationBlockReason)
    case authorized(LocationAuthorizationScope)
}

enum LocationAccuracyAuthorization {
    case full
    case reduced
}
