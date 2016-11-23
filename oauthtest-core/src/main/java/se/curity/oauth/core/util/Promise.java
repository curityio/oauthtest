package se.curity.oauth.core.util;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * The result of an asynchronous computation.
 */
public final class Promise<Success, Failure>
{

    public static final class Deferred<Success, Failure>
    {
        private final Promise<Success, Failure> _promise = new Promise<>(this);
        private volatile Success _success;
        private volatile Failure _failure;

        public void fullfill(Success success)
        {
            _success = success;

            @Nullable Consumer<Success> successHandler = _promise._successHandler;

            if (successHandler != null)
            {
                successHandler.accept(success);
            }
        }

        public void fail(@Nullable Failure failure)
        {
            _failure = failure;

            @Nullable Consumer<Failure> failureHandler = _promise._failureHandler;

            if (failureHandler != null)
            {
                failureHandler.accept(failure);
            }
        }

        public Promise<Success, Failure> getPromise()
        {
            return _promise;
        }

        private void onFailureHandlerAdded(Consumer<Failure> failureHandler)
        {
            Failure failure = _failure;

            if (failure != null)
            {
                failureHandler.accept(failure);
            }
        }

        private void onSuccessHandlerAdded(Consumer<Success> successHandler)
        {
            Success success = _success;

            if (success != null)
            {
                successHandler.accept(success);
            }
        }
    }

    private final Deferred<Success, Failure> _deferred;

    @Nullable
    private volatile Consumer<Success> _successHandler = null;

    @Nullable
    private volatile Consumer<Failure> _failureHandler = null;

    private Promise(Deferred<Success, Failure> deferred)
    {
        _deferred = deferred;
    }

    /**
     * Set the success handler. This method will be called as soon as the promise is fulfilled,
     * or immediately if the Promise was fulfilled before this method was called.
     *
     * @param successHandler to run on success
     * @return this Promise
     */
    public Promise<Success, Failure> onSuccess(Consumer<Success> successHandler)
    {
        _successHandler = successHandler;
        _deferred.onSuccessHandlerAdded(successHandler);
        return this;
    }

    /**
     * Set the failure handler. This method will be called as soon as the promise fails,
     * or immediately if the Promise already failed before this method was called.
     *
     * @param failureHandler to run on failure
     * @return this Promise
     */
    public Promise<Success, Failure> onFailure(Consumer<Failure> failureHandler)
    {
        _failureHandler = failureHandler;
        _deferred.onFailureHandlerAdded(failureHandler);
        return this;
    }

}

