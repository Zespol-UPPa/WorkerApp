package parkflow.deskoptworker.Controllers.Admin;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import lombok.Setter;
import parkflow.deskoptworker.Controllers.Components.ParkingItemController;
import parkflow.deskoptworker.Controllers.Refreshable;
import parkflow.deskoptworker.Views.ViewFactory;
import parkflow.deskoptworker.api.ParkingService;
import parkflow.deskoptworker.utils.SessionManager;
import parkflow.deskoptworker.models.Parking;
import parkflow.deskoptworker.models.UserRole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingsController implements Refreshable {
    @FXML private TextField searchField;
    @FXML private Button addParkingBtn;
    @FXML private FlowPane parkingsContainer;
    @FXML private HBox paginationBox;
    @FXML private HBox dotsContainer;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label loadingLabel; // Optional - dla "Loading..."

    private List<Parking> allParkings;
    private List<Parking> filteredParkings;
    private int currentPage = 0;
    private int totalPages = 0;
    private int itemsPerPage = 4;
    private static final int MAX_VISIBLE_DOTS = 5;

    private UserRole currentUserRole;
    private final ParkingService parkingService = new ParkingService();

    @Setter
    private ViewFactory viewFactory;

    @FXML
    public void initialize() {

        if (SessionManager.getInstance().getCurrentUser() == null) {
            System.out.println("No active session – redirecting to login");
            new ViewFactory().showLoginWindow();
            return;
        }

        // Pobierz rolę z sesji użytkownika
        currentUserRole = SessionManager
                .getInstance()
                .getCurrentUser()
                .getRole();

        // Konfiguruj widok na podstawie roli
        configureViewForRole();

        // Załaduj parkingi z backendu
        loadParkingsFromBackend();

        // Listeners
        parkingsContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                adjustItemsPerPageBasedOnHeight();
            }
        });

        parkingsContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                showPage(currentPage);
            }
        });

        searchField.textProperty().addListener(
                (obs, oldVal, newVal) -> filterParkings(newVal)
        );
    }

    private void configureViewForRole() {
        if (currentUserRole == UserRole.ADMIN) {
            addParkingBtn.setVisible(true);
            addParkingBtn.setManaged(true);
        } else {
            addParkingBtn.setVisible(false);
            addParkingBtn.setManaged(false);
        }
    }

    @FXML
    private void handleAddParking() {
        viewFactory.showAddParkingModal();
    }

    /**
     * Load parkings from backend in background thread
     */
    private void loadParkingsFromBackend() {
        // Show loading indicator (optional)
        showLoadingState(true);

        new Thread(() -> {
            try {
                // Fetch parkings from backend
                List<Parking> parkings = parkingService.getCompanyParkings();

                // Update UI on JavaFX thread
                Platform.runLater(() -> {
                    if (parkings != null && !parkings.isEmpty()) {
                        allParkings = parkings;
                        filteredParkings = new ArrayList<>(allParkings);
                        totalPages = (int) Math.ceil((double) filteredParkings.size() / itemsPerPage);
                        showPage(0);
                        System.out.println("Loaded " + parkings.size() + " parkings from backend");
                    } else {
                        allParkings = new ArrayList<>();
                        filteredParkings = new ArrayList<>();
                        totalPages = 0;
                        System.out.println("No parkings found for this company");
                    }
                    showLoadingState(false);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    System.err.println("Failed to load parkings: " + e.getMessage());
                    e.printStackTrace();
                    allParkings = new ArrayList<>();
                    filteredParkings = new ArrayList<>();
                    totalPages = 0;
                    showLoadingState(false);
                });
            }
        }).start();
    }

    /**
     * Show/hide loading indicator
     */
    private void showLoadingState(boolean loading) {
        if (loadingLabel != null) {
            loadingLabel.setVisible(loading);
            loadingLabel.setManaged(loading);
        }
        parkingsContainer.setDisable(loading);
        searchField.setDisable(loading);
    }

    private void adjustItemsPerPageBasedOnHeight() {
        double availableHeight = parkingsContainer.getHeight();

        if (availableHeight <= 0) {
            itemsPerPage = 4; // Default: 2x2 layout
            return;
        }

        double cardHeight = 300; // Card height including spacing
        double vgap = 20;
        double padding = 20;

        // Calculate how many rows fit
        int rows = Math.max(1, (int) ((availableHeight - padding + vgap) / (cardHeight + vgap)));

        // 2 cards per row (2x2 layout)
        int cardsPerRow = 2;
        itemsPerPage = rows * cardsPerRow;

        // Minimum 4 cards (2x2), maximum 8 cards (4x2)
        itemsPerPage = Math.max(4, Math.min(8, itemsPerPage));

        totalPages = (int) Math.ceil((double) filteredParkings.size() / itemsPerPage);

        if (currentPage >= totalPages) {
            currentPage = Math.max(0, totalPages - 1);
        }

        showPage(currentPage);
    }

    private void filterParkings(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredParkings = new ArrayList<>(allParkings);
        } else {
            String search = searchText.toLowerCase().trim();
            filteredParkings = allParkings.stream()
                    .filter(p ->
                            String.valueOf(p.getId()).contains(search) ||
                                    p.getName().toLowerCase().contains(search) ||
                                    p.getAddress().toLowerCase().contains(search)
                    )
                    .collect(Collectors.toList());
        }

        totalPages = (int) Math.ceil((double) filteredParkings.size() / itemsPerPage);
        showPage(0);
    }

    private void showPage(int pageIndex) {
        if (pageIndex < 0 || pageIndex >= totalPages) return;

        currentPage = pageIndex;
        parkingsContainer.getChildren().clear();

        int start = pageIndex * itemsPerPage;
        int end = Math.min(start + itemsPerPage, filteredParkings.size());

        double containerWidth = parkingsContainer.getWidth();
        double gap = 20;
        // 2 cards per row (2x2 layout for 4 cards total)
        // Wider cards - minimum 480px for better text display
        double cardWidth = Math.max(480.0, (containerWidth - gap) / 2.0);
        cardWidth = cardWidth - 10; // Small margin adjustment

        for (int i = start; i < end; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/parkingItem.fxml"));
                VBox card = loader.load();

                ParkingItemController controller = loader.getController();
                controller.setData(filteredParkings.get(i), currentUserRole);
                controller.setViewFactory(viewFactory);

                card.setMinWidth(cardWidth);
                card.setPrefWidth(cardWidth);
                card.setMaxWidth(cardWidth);

                parkingsContainer.getChildren().add(card);
            } catch (IOException e) {
                System.err.println("Couldn't find component parkingItem.fxml");
                e.printStackTrace();
            }
        }

        updatePagination();
    }

    @FXML
    private void previousPage() {
        if (currentPage > 0) {
            showPage(currentPage - 1);
        }
    }

    @FXML
    private void nextPage() {
        if (currentPage < totalPages - 1) {
            showPage(currentPage + 1);
        }
    }

    private void updatePagination() {
        dotsContainer.getChildren().clear();

        prevButton.setDisable(currentPage == 0);
        nextButton.setDisable(currentPage == totalPages - 1);

        if (totalPages <= MAX_VISIBLE_DOTS) {
            for (int i = 0; i < totalPages; i++) {
                dotsContainer.getChildren().add(createDot(i));
            }
        } else {
            createSmartPagination();
        }
    }

    private void createSmartPagination() {
        int startPage, endPage;

        if (currentPage < 3) {
            startPage = 0;
            endPage = 4;

            for (int i = startPage; i <= endPage; i++) {
                dotsContainer.getChildren().add(createDot(i));
            }

            if (totalPages > 5) {
                dotsContainer.getChildren().add(createEllipsis());
                dotsContainer.getChildren().add(createPageNumber(totalPages - 1));
            }

        } else if (currentPage > totalPages - 4) {
            dotsContainer.getChildren().add(createPageNumber(0));
            dotsContainer.getChildren().add(createEllipsis());

            startPage = totalPages - 5;
            endPage = totalPages - 1;

            for (int i = startPage; i <= endPage; i++) {
                dotsContainer.getChildren().add(createDot(i));
            }

        } else {
            dotsContainer.getChildren().add(createPageNumber(0));
            dotsContainer.getChildren().add(createEllipsis());

            for (int i = currentPage - 1; i <= currentPage + 1; i++) {
                dotsContainer.getChildren().add(createDot(i));
            }

            dotsContainer.getChildren().add(createEllipsis());
            dotsContainer.getChildren().add(createPageNumber(totalPages - 1));
        }
    }

    private Circle createDot(int pageIndex) {
        Circle dot = new Circle(6);
        dot.getStyleClass().add("pagination-dot");

        if (pageIndex == currentPage) {
            dot.getStyleClass().add("pagination-dot-active");
        } else {
            dot.getStyleClass().add("pagination-dot-inactive");
        }

        dot.setOnMouseClicked(e -> showPage(pageIndex));

        return dot;
    }

    private Label createPageNumber(int pageIndex) {
        Label label = new Label(String.valueOf(pageIndex + 1));
        label.getStyleClass().add("pagination-number");

        if (pageIndex == currentPage) {
            label.getStyleClass().add("pagination-number-active");
        }

        label.setOnMouseClicked(e -> showPage(pageIndex));

        return label;
    }

    private Label createEllipsis() {
        Label ellipsis = new Label("...");
        ellipsis.getStyleClass().add("pagination-ellipsis");
        return ellipsis;
    }

    /**
     * Refresh parkings list (can be called after adding/editing parkings)
     */
    public void refreshParkings() {
        loadParkingsFromBackend();
    }

    /**
     * Implementation of Refreshable interface
     * Called automatically when view becomes visible
     */
    @Override
    public void refresh() {
        System.out.println("ParkingsController: Refreshing data from backend...");
        refreshParkings();
    }
}