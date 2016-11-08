package se.curity.oauth.core.util;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Either represents the result of a computation which may be either successful or a failure.
 */
public class Either<Success, Failure>
{
    private final Success _success;
    private final Failure _failure;

    public static <S, F> Either<S, F> success(S success)
    {
        return new Either<>(success, null);
    }

    public static <S, F> Either<S, F> failure(F failure)
    {
        return new Either<>(null, failure);
    }

    private Either(@Nullable Success success,
                   @Nullable Failure failure)
    {
        _success = success;
        _failure = failure;
    }

    public <T> T onResult(Function<Success, T> successHandler,
                          Function<Failure, T> failureHandler)
    {
        if (_success != null)
        {
            return successHandler.apply(_success);
        }

        assert _failure != null; // this is ensured in the constructor
        return failureHandler.apply(_failure);
    }

}
