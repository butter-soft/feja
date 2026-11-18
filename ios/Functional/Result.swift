func map<Input, Output, Failure: Error>(
    _ transform: @escaping (Input) -> Output
) -> (Result<Input, Failure>) -> Result<Output, Failure> {
    {
        result in result.map(transform)
    }
}

func mapError<Value, Failure: Error, NewFailure: Error>(
    _ transform: @escaping (Failure) -> NewFailure
) -> (Result<Value, Failure>) -> Result<Value, NewFailure> {
    {
        result in result.mapError(transform)
    }
}

func flatMap<Input, Output, Failure: Error>(
    _ transform: @escaping (Input) -> Result<Output, Failure>
) -> (Result<Input, Failure>) -> Result<Output, Failure> {
    {
        result in result.flatMap(transform)
    }
}

func flatMapError<Value, Failure: Error, NewFailure: Error>(
    _ transform: @escaping (Failure) -> Result<Value, NewFailure>
) -> (Result<Value, Failure>) -> Result<Value, NewFailure> {
    {
        result in result.flatMapError(transform)
    }
}

func zip<Left, Right, Failure: Error>(
    _ left: Result<Left, Failure>,
    _ right: Result<Right, Failure>
) -> Result<(Left, Right), Failure> {
    switch (left, right) {
    case (.success(let left), .success(let right)):
        return .success((left, right))
    case (.failure(let error), _):
        return .failure(error)
    case (_, .failure(let error)):
        return .failure(error)
    }
}

func apply<Input, Output, Failure: Error>(_ input: Result<Input, Failure>) -> (
    Result<(Input) -> Output, Failure>
) -> Result<Output, Failure> {
    {
        transform in
        zip(transform, input)
            |> map { function, value in function(value)

            }
    }
}
