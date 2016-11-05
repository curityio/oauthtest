package se.curity.oauth.core.util;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 *
 */
public class Workers {

    private static final Thread.UncaughtExceptionHandler defaultExceptionHandler = ( t, e ) -> {
        System.err.println( "Error on Thread: " + t.getName() );
        e.printStackTrace();
    };

    private final ThreadFactory threadFactory = r -> {
        Thread t = new Thread( r );
        t.setDaemon( true );
        t.setUncaughtExceptionHandler( defaultExceptionHandler );
        return t;
    };

    private final ExecutorService executor = Executors.newCachedThreadPool( threadFactory );

    public <T> Service<T> createService( Task<T> runnable ) {
        Service<T> service = new Service<T>() {
            @Override
            protected Task<T> createTask() {
                return runnable;
            }
        };
        service.setExecutor( executor );
        return service;
    }

}
