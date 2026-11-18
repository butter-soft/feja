func curry<First, Second, Third, Output>(
    _ transform: @escaping (First, Second, Third) -> Output
) -> (First) -> (Second) -> (Third) -> Output {
    { first in
        { second in
            { third in
                transform(first, second, third)
            }
        }
    }
}
