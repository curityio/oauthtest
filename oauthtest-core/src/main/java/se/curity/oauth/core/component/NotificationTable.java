package se.curity.oauth.core.component;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import se.curity.oauth.core.util.event.EventBus;
import se.curity.oauth.core.util.event.Notification;

/**
 * A table that displays {@link Notification}s.
 * <p>
 * It subscribes to Notification events automatically.
 */
public class NotificationTable
{

    @FXML
    private TextField filterText;
    @FXML
    private TableView<Notification> notificationTable;
    @FXML
    private TableColumn<Notification, String> notificationLevelColumn;
    @FXML
    private TableColumn<Notification, String> notificationMessageColumn;

    private final EventBus _eventBus;
    private final ObservableList<Notification> _notifications = FXCollections.observableArrayList();

    public NotificationTable(EventBus eventBus)
    {
        _eventBus = eventBus;
    }

    @FXML
    protected void initialize()
    {
        notificationLevelColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getLevel().name()));
        notificationMessageColumn.setCellValueFactory(data ->
                new ReadOnlyStringWrapper(data.getValue().getText()));

        FilteredList<Notification> filteredNotifications = new FilteredList<>(_notifications);

        notificationTable.setItems(filteredNotifications);

        filterText.textProperty().addListener((observable, oldValue, newValue) ->
                filteredNotifications.setPredicate(notification -> newValue == null ||
                        newValue.isEmpty() ||
                        includeNotification(notification, newValue)));

        _eventBus.subscribe(Notification.class, _notifications::add);
    }

    private static boolean includeNotification(Notification notification, String filter)
    {
        String lowerCaseFilter = filter.toLowerCase();
        return notification.getLevel().name().toLowerCase().contains(lowerCaseFilter) ||
                notification.getText().toLowerCase().contains(lowerCaseFilter);
    }

}