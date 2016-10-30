package com.athaydes.oauth.core.component;

import com.athaydes.oauth.core.util.Workers;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.util.List;

/**
 *
 */
public class Arrows {

    public interface Step<T> {
        String name();

        Service<T> service();

        static <T> Step<T> create( String name, Service<T> service ) {
            return new Step<T>() {
                @Override
                public String name() {
                    return name;
                }

                @Override
                public Service<T> service() {
                    return service;
                }
            };
        }
    }

    @FXML
    private Text currentStep;
    @FXML
    private Button backButton;
    @FXML
    private Button nextButton;

    private final ObservableList<Step> steps = FXCollections.observableArrayList();

    private int step = 0;

    private final Workers workers;

    public Arrows( Workers workers ) {
        this.workers = workers;
    }

    @FXML
    private void initialize() {
        steps.addListener( new InvalidationListener() {
            @Override
            public void invalidated( Observable observable ) {
                step = 0;
                resetComponents();
            }
        } );
    }

    @FXML
    private void previous() {
        if ( step > 0 ) {
            step--;
            resetComponents();

            // TODO change screen
        }
    }

    @FXML
    private void next() {
        if ( step < steps.size() ) {
            Step runnableStep = steps.get( step );
            currentStep.setText( "Running..." );
            Service<?> service = runnableStep.service();
            service.setOnSucceeded( event -> {
                System.out.println( "SUCCESS" );
                step++;
                currentStep.setText( runnableStep.name() );
                // TODO change screens
            } );
            service.setOnFailed( event -> {
                System.err.println( "FAIL" );
            } );
            service.setOnReady( event -> {
                System.out.println( "READY" );
                resetComponents();
            } );

            service.start();
        }
    }

    public void setSteps( List<Step> steps ) {
        this.steps.clear();
        this.steps.addAll( steps );
        this.steps.add( new DoneStep() );
        resetComponents();
    }

    private void resetComponents() {
        backButton.setDisable( step == 0 || steps.isEmpty() );
        nextButton.setDisable( step >= steps.size() );
        if ( 0 <= step && step < steps.size() ) {
            currentStep.setText( steps.get( step ).name() );
        }
    }

    private static class DoneStep implements Step<Object> {
        @Override
        public String name() {
            return "Done!";
        }

        @Override
        public Service<Object> service() {
            throw new UnsupportedOperationException( "The last step cannot be run" );
        }
    }

}
