package se.curity.oauth.core.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 *
 */
public class Workers
{

    private static final Thread.UncaughtExceptionHandler defaultExceptionHandler = (t, e) ->
    {
        System.err.println("Error on Thread: " + t.getName());
        e.printStackTrace();
    };

    private final ThreadFactory threadFactory = r ->
    {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.setUncaughtExceptionHandler(defaultExceptionHandler);
        return t;
    };

    private final ExecutorService executor = Executors.newCachedThreadPool(threadFactory);

    public <S> Promise<S, Void> runInBackground(Callable<S> supplier)
    {
        Promise.Deferred<S, Void> deferred = new Promise.Deferred<>();

        executor.submit(() ->
        {
            try
            {
                deferred.fullfill(supplier.call());
            }
            catch (Exception e)
            {
                deferred.fail(null);
            }
        });

        return deferred.getPromise();
    }

}
