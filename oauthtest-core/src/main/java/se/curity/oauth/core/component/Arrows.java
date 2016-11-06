package se.curity.oauth.core.component;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import se.curity.oauth.core.util.event.EventBus;
import se.curity.oauth.core.util.event.Notification;

import java.util.List;

/**
 * Back/Forward arrow buttons to control actions that have many steps.
 */
public class Arrows
{

    public interface Step<T>
    {
        String name();

        Service<T> service();

        static <T> Step<T> create(String name, Service<T> service)
        {
            return new Step<T>()
            {
                @Override
                public String name()
                {
                    return name;
                }

                @Override
                public Service<T> service()
                {
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
    private final EventBus eventBus;

    public Arrows(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    @FXML
    private void initialize()
    {
        steps.addListener(new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                step = 0;
                resetComponents();
            }
        });
    }

    @FXML
    private void previous()
    {
        if (step > 0)
        {
            step--;
            resetComponents();
        }
    }

    @FXML
    private void next()
    {
        if (step < steps.size())
        {
            backButton.setDisable(true);
            nextButton.setDisable(true);

            Step runnableStep = steps.get(step);
            currentStep.setText("Running...");
            Service<?> service = runnableStep.service();
            service.setOnSucceeded(event ->
            {
                System.out.println("SUCCESS");
                step++;
                service.reset();
            });
            service.setOnFailed(event ->
            {
                System.out.println("FAIL");

                @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
                Throwable error = event.getSource().getException();

                String errorMessage = (error.getMessage() != null) ?
                        error.getMessage() :
                        error.toString();

                resetComponents();
                eventBus.publish(new Notification(Notification.Level.WARNING,
                        "Step '" + runnableStep.name() + "' failed: " + errorMessage));
                service.reset();
            });
            service.setOnReady(event ->
            {
                System.out.println("READY");
                resetComponents();
            });

            try
            {
                service.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                resetComponents();
            }
        }
    }

    public void setSteps(List<Step> steps)
    {
        this.steps.clear();
        this.steps.addAll(steps);
        this.steps.add(new DoneStep());
        resetComponents();
    }

    private void resetComponents()
    {
        backButton.setDisable(step == 0 || steps.isEmpty());
        nextButton.setDisable(step >= steps.size() - 1);
        if (0 <= step && step < steps.size() - 1)
        {
            currentStep.setText(steps.get(step).name());
        }
    }

    private static class DoneStep implements Step<Object>
    {
        @Override
        public String name()
        {
            return "Done!";
        }

        @Override
        public Service<Object> service()
        {
            throw new UnsupportedOperationException("The last step cannot be run");
        }
    }

}
