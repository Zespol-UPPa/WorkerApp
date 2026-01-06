package parkflow.deskoptworker.Controllers.Components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import lombok.Setter;

public class FilterBarPaymentsController {

    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> statusCombo;

    @Setter
    private FilterChangeListener filterChangeListener;

    public interface FilterChangeListener {
        void onFiltersChanged(String type, String status);
    }

    @FXML
    public void initialize() {
        setupTypeFilter();
        setupStatusFilter();
        setupListeners();
    }

    private void setupTypeFilter() {
        ObservableList<String> types = FXCollections.observableArrayList(
                "All Types",
                "Parking Session",
                "Reservation Fee"
        );
        typeCombo.setItems(types);
        typeCombo.setValue("All Types");
    }

    private void setupStatusFilter() {
        ObservableList<String> statuses = FXCollections.observableArrayList(
                "All Statuses",
                "Completed",
                "Pending"
        );
        statusCombo.setItems(statuses);
        statusCombo.setValue("All Statuses");
    }

    private void setupListeners() {
        typeCombo.setOnAction(_ -> notifyFilterChange());
        statusCombo.setOnAction(_ -> notifyFilterChange());
    }

    private void notifyFilterChange() {
        if (filterChangeListener != null) {
            filterChangeListener.onFiltersChanged(
                    typeCombo.getValue(),
                    statusCombo.getValue()
            );
        }
    }

    public String getSelectedType() {
        return typeCombo.getValue();
    }

    public String getSelectedStatus() {
        return statusCombo.getValue();
    }

    /**
     * Resetuje filtry do domyślnych wartości
     */
    public void resetFilters() {
        typeCombo.setValue("All Types");
        statusCombo.setValue("All Statuses");
    }
}