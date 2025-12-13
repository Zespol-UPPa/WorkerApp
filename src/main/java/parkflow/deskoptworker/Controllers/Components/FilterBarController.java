package parkflow.deskoptworker.Controllers.Components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class FilterBarController {

    @FXML private ComboBox<String> timePeriodCombo;
    @FXML private ComboBox<String> parkingCombo;

    private FilterChangeListener filterChangeListener;

    // Interface dla komunikacji z parent controllerem
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
        timePeriodCombo.setValue("This Month"); // Default
    }

    private void setupParkingFilter() {
        ObservableList<String> parkings = FXCollections.observableArrayList(
                "All parkings",
                "Galeria Krakowska",
                "CH Bonarka",
                "Podwawelski",
                "Downtown Plaza"
        );
        parkingCombo.setItems(parkings);
        parkingCombo.setValue("All parkings"); // Default
    }

    private void setupListeners() {
        // Auto-apply on selection change
        timePeriodCombo.setOnAction(e -> notifyFilterChange());
        parkingCombo.setOnAction(e -> notifyFilterChange());
    }

    private void notifyFilterChange() {
        if (filterChangeListener != null) {
            String timePeriod = timePeriodCombo.getValue();
            String parking = parkingCombo.getValue();
            filterChangeListener.onFiltersChanged(timePeriod, parking);
        }
    }

    // Public setters
    public void setFilterChangeListener(FilterChangeListener listener) {
        this.filterChangeListener = listener;
    }

    public String getSelectedTimePeriod() {
        return timePeriodCombo.getValue();
    }

    public String getSelectedParking() {
        return parkingCombo.getValue();
    }
}