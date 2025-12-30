package parkflow.deskoptworker.Controllers.Components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.Getter;
import lombok.Setter;

public class FilterBarReservationsController {

    @FXML private Button filterAllBtn;
    @FXML private Button filterActiveBtn;
    @FXML private Button filterUpcomingBtn;
    @FXML private Button filterHistoryBtn;

    @Setter
    private FilterBarListener listener;

    @Getter
    private String currentFilter = "all";

    public interface FilterBarListener {
        void onFilterChanged(String filter);
    }

    @FXML
    public void initialize() {
        // Set initial state
        updateButtonStyles();
    }

    @FXML
    private void onFilterAll() {
        currentFilter = "all";
        updateButtonStyles();
        notifyListener();
    }

    @FXML
    private void onFilterActive() {
        currentFilter = "active";
        updateButtonStyles();
        notifyListener();
    }

    @FXML
    private void onFilterUpcoming() {
        currentFilter = "upcoming";
        updateButtonStyles();
        notifyListener();
    }

    @FXML
    private void onFilterHistory() {
        currentFilter = "completed";
        updateButtonStyles();
        notifyListener();
    }

    private void updateButtonStyles() {
        filterAllBtn.getStyleClass().remove("filter-btn-active");
        filterActiveBtn.getStyleClass().remove("filter-btn-active");
        filterUpcomingBtn.getStyleClass().remove("filter-btn-active");
        filterHistoryBtn.getStyleClass().remove("filter-btn-active");

        switch (currentFilter) {
            case "all" -> filterAllBtn.getStyleClass().add("filter-btn-active");
            case "active" -> filterActiveBtn.getStyleClass().add("filter-btn-active");
            case "upcoming" -> filterUpcomingBtn.getStyleClass().add("filter-btn-active");
            case "completed" -> filterHistoryBtn.getStyleClass().add("filter-btn-active");
        }
    }

    private void notifyListener() {
        if (listener != null) {
            listener.onFilterChanged(currentFilter);
        }
    }

    /**
     * Programmatically set filter (e.g., when resetting view)
     */
    public void setFilter(String filter) {
        this.currentFilter = filter;
        updateButtonStyles();
    }

}