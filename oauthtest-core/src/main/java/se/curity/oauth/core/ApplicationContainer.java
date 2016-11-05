package se.curity.oauth.core;

import se.curity.oauth.core.component.MessagePopup;
import se.curity.oauth.core.util.Workers;
import se.curity.oauth.core.util.event.EventBus;
import javafx.stage.Stage;
import org.picocontainer.Characteristics;
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
        container.addComponent( primaryStage );
        container.as( Characteristics.CACHE )
                .addComponent( MessagePopup.class )
                // force immediate instantiation
                .getComponent( MessagePopup.class );
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
