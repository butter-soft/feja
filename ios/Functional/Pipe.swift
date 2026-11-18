precedencegroup ForwardPipePrecedence {
    associativity: left
    higherThan: AssignmentPrecedence
    lowerThan: TernaryPrecedence
}

infix operator |> : ForwardPipePrecedence

func |> <Input, Output>(value: Input, transform: (Input) -> Output) -> Output {
    transform(value)
}
