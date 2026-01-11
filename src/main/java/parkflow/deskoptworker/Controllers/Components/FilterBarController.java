package parkflow.deskoptworker.Controllers.Components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Setter;

import java.util.List;

public class FilterBarController {

    @FXML private ComboBox<String> timePeriodCombo;
    @FXML private ComboBox<String> parkingCombo;
    @FXML private HBox parkingFilterContainer; // Container for parking filter
    @FXML private Label parkingLabel; // Label for parking

    @Setter
    private FilterChangeListener filterChangeListener;

    public interface FilterChangeListener {
        void onFiltersChanged(String timePeriod, String parking);
    }

    @FXML
    public void initialize() {
        setupTimePeriodFilter();
        setupParkingFilter();
        setupListeners();
    }

    private void setupTimePeriodFilter() {
        ObservableList<String> timePeriods = FXCollections.observableArrayList(
                "Today",
                "Yesterday",
                "This Week",
                "Last Week",
                "This Month",
                "Last Month",
                "This Quarter",
                "This Year"
        );
        timePeriodCombo.setItems(timePeriods);
        timePeriodCombo.setValue("This Month");
    }

    private void setupParkingFilter() {
        // Start with just "All parkings" - will be populated later
        ObservableList<String> parkings = FXCollections.observableArrayList("All parkings");
        parkingCombo.setItems(parkings);
        parkingCombo.setValue("All parkings");
    }

    /**
     * Populate parking filter with actual parking names
     * Call this from parent controller after loading parkings
     */
    public void setParkings(List<String> parkingNames) {
        ObservableList<String> parkings = FXCollections.observableArrayList("All parkings");
        parkings.addAll(parkingNames);
        parkingCombo.setItems(parkings);
        parkingCombo.setValue("All parkings");
    }

    private void setupListeners() {
        timePeriodCombo.setOnAction(_ -> notifyFilterChange());
        parkingCombo.setOnAction(_ -> notifyFilterChange());
    }

    private void notifyFilterChange() {
        if (filterChangeListener != null) {
            String timePeriod = timePeriodCombo.getValue();
            String parking = parkingCombo.getValue();
            filterChangeListener.onFiltersChanged(timePeriod, parking);
        }
    }

    /**
     * Hide parking filter (for workers)
     */
    public void hideParkingFilter() {
        if (parkingCombo != null) {
            parkingCombo.setVisible(false);
            parkingCombo.setManaged(false);
        }
        if (parkingLabel != null) {
            parkingLabel.setVisible(false);
            parkingLabel.setManaged(false);
        }
        if (parkingFilterContainer != null) {
            parkingFilterContainer.setVisible(false);
            parkingFilterContainer.setManaged(false);
        }
    }

    /**
     * Show parking filter (for admins)
     */
    public void showParkingFilter() {
        if (parkingCombo != null) {
            parkingCombo.setVisible(true);
            parkingCombo.setManaged(true);
        }
        if (parkingLabel != null) {
            parkingLabel.setVisible(true);
            parkingLabel.setManaged(true);
        }
        if (parkingFilterContainer != null) {
            parkingFilterContainer.setVisible(true);
            parkingFilterContainer.setManaged(true);
        }
    }

    public String getSelectedTimePeriod() {
        return timePeriodCombo.getValue();
    }

    public String getSelectedParking() {
        return parkingCombo.getValue();
    }
}