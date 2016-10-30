package com.athaydes.oauth.core;

import com.athaydes.oauth.core.util.Workers;
import com.athaydes.oauth.core.util.event.EventBus;
import javafx.stage.Stage;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;

/**
 * Dependency Injection Container.
 */
class ApplicationContainer {

    private final MutablePicoContainer container = new DefaultPicoContainer();

    ApplicationContainer( Stage primaryStage ) {
        container.addComponent( new Workers() );
        container.addComponent( new EventBus() );
    }

    Object get( Class<?> type ) {
        Object instance = container.getComponent( type );
        if ( instance == null ) {
            container.addComponent( type );
            return container.getComponent( type );
        } else {
            return instance;
        }
    }

}
