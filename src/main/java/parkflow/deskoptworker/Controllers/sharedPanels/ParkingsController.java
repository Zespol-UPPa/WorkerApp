package parkflow.deskoptworker.Controllers.sharedPanels;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import parkflow.deskoptworker.Controllers.Components.ParkingItemController;
import parkflow.deskoptworker.SessionManager;
import parkflow.deskoptworker.models.Parking;
import parkflow.deskoptworker.models.UserRole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParkingsController {
    @FXML private TextField searchField;
    @FXML private Button addParkingBtn;
    @FXML private FlowPane parkingsContainer;
    @FXML private HBox paginationBox;
    @FXML private HBox dotsContainer;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    private List<Parking> allParkings;
    private List<Parking> filteredParkings;
    private int currentPage = 0;
    private int totalPages = 0;
    private int itemsPerPage = 4;
    private static final int MAX_VISIBLE_DOTS = 5;

    private UserRole currentUserRole; // Pobierz z sesji/loginu

    @FXML
    public void initialize() {
        // Pobierz rolę z sesji użytkownika
        currentUserRole = SessionManager.getCurrentUser().getRole();

        // Konfiguruj widok na podstawie roli
        configureViewForRole();

        // Załaduj parkingi
        loadParkings();

        //listener do wysokosci
        parkingsContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                adjustItemsPerPageBasedOnHeight();
            }
        });

        parkingsContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                showPage(currentPage); // Przeładuj z nową szerokością
            }
        });

        // Search listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterParkings(newVal));
    }

    /**
     * Konfiguruje widok na podstawie roli użytkownika
     */
    private void configureViewForRole() {
        if (currentUserRole == UserRole.ADMIN) {
            // Admin widzi przycisk dodawania
            addParkingBtn.setVisible(true);
            addParkingBtn.setManaged(true);
        } else {
            // Worker nie widzi przycisku
            addParkingBtn.setVisible(false);
            addParkingBtn.setManaged(false);
        }
    }

    /**
     * Obsługa dodawania nowego parkingu (tylko Admin)
     */
    @FXML
    private void handleAddParking() {
        System.out.println("Opening Add Parking dialog...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/admin/addNewParking.fxml"));
            Parent root = loader.load();
            Stage addParkingStage = new Stage();
            addParkingStage.setTitle("New parking");
            addParkingStage.setScene(new Scene(root));

            addParkingStage.setWidth(782);
            addParkingStage.setHeight(780);


            // Zablokuj zmianę rozmiaru
            addParkingStage.setResizable(false);

            // Ustaw jako modalne (blokuje główne okno)
            addParkingStage.initModality(Modality.APPLICATION_MODAL);

            // Ustaw właściciela (parent window)
            addParkingStage.initOwner(addParkingBtn.getScene().getWindow());

            // Wyśrodkuj okno
            addParkingStage.centerOnScreen();

            // Pokaż okno i czekaj aż się zamknie
            addParkingStage.showAndWait();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void adjustItemsPerPageBasedOnHeight() {
        double availableHeight = parkingsContainer.getHeight();

        if (availableHeight <= 0) {
            // Użyj domyślnej wartości
            itemsPerPage = 6;
            return;
        }

        // Parametry karty
        double cardHeight = 280;    // Przybliżona wysokość karty z paddingiem
        double vgap = 20;           // Vertical gap z FlowPane
        double padding = 20;        // Top + bottom padding

        // Oblicz ile WIERSZY zmieści się
        int rows = Math.max(1, (int) ((availableHeight - padding + vgap) / (cardHeight + vgap)));

        // Ustal itemsPerPage na podstawie wierszy
        // Zakładamy przeciętnie 3-4 kolumny (FlowPane sam to ustali)
        int avgCardsPerRow = 3;  // Konserwatywne założenie
        itemsPerPage = rows * avgCardsPerRow;

        // Minimalna i maksymalna wartość
        itemsPerPage = Math.max(2, Math.min(20, itemsPerPage));

        System.out.println("Wysokość: " + availableHeight +
                ", Wiersze: " + rows +
                ", itemsPerPage: " + itemsPerPage);

        // Przelicz strony
        totalPages = (int) Math.ceil((double) filteredParkings.size() / itemsPerPage);

        // Upewnij się, że currentPage jest poprawna
        if (currentPage >= totalPages) {
            currentPage = Math.max(0, totalPages - 1);
        }

        // Odśwież widok
        showPage(currentPage);
    }

    private void loadParkings() {
        // Przykładowe dane - później zamień na pobieranie z bazy
        allParkings = Arrays.asList(
                new Parking(98, "Galeria Krakowska", "Kraków", 1500, 100, 15, 0.10, 5.0),
                new Parking(99, "Parking Centrum", "Warszawa", 800, 750, 20, 0.15, 7.5),
                new Parking(100, "Mall Gdańsk", "Gdańsk", 50, 10, 10, 0.08, 3.0),
                new Parking(101, "Plaza Wrocław", "Wrocław", 1200, 900, 15, 0.12, 6.0),
                new Parking(102, "City Park Poznań", "Poznań", 600, 450, 15, 0.10, 5.0),
                new Parking(103, "Arkadia", "Warszawa", 2000, 1800, 20, 0.15, 8.0),
                new Parking(104, "Silesia", "Katowice", 1800, 200, 15, 0.10, 5.5),
                new Parking(105, "Złote Tarasy", "Warszawa", 900, 800, 15, 0.12, 6.5),
                new Parking(106, "Manufaktura", "Łódź", 1100, 950, 20, 0.11, 5.0),
                new Parking(107, "Pasaż Grunwaldzki", "Wrocław", 750, 600, 15, 0.10, 5.0),
                new Parking(108, "CH Auchan", "Kraków", 500, 50, 10, 0.09, 4.0),
                new Parking(109, "Westfield Mokotów", "Warszawa", 1300, 1100, 20, 0.14, 7.0),
                new Parking(98, "Galeria Krakowska", "Kraków", 1500, 100, 15, 0.10, 5.0),
                new Parking(99, "Parking Centrum", "Warszawa", 800, 750, 20, 0.15, 7.5),
                new Parking(100, "Mall Gdańsk", "Gdańsk", 50, 10, 10, 0.08, 3.0),
                new Parking(101, "Plaza Wrocław", "Wrocław", 1200, 900, 15, 0.12, 6.0),
                new Parking(102, "City Park Poznań", "Poznań", 600, 450, 15, 0.10, 5.0),
                new Parking(103, "Arkadia", "Warszawa", 2000, 1800, 20, 0.15, 8.0),
                new Parking(104, "Silesia", "Katowice", 1800, 200, 15, 0.10, 5.5),
                new Parking(105, "Złote Tarasy", "Warszawa", 900, 800, 15, 0.12, 6.5),
                new Parking(106, "Manufaktura", "Łódź", 1100, 950, 20, 0.11, 5.0),
                new Parking(107, "Pasaż Grunwaldzki", "Wrocław", 750, 600, 15, 0.10, 5.0),
                new Parking(108, "CH Auchan", "Kraków", 500, 50, 10, 0.09, 4.0),
                new Parking(109, "Westfield Mokotów", "Warszawa", 1300, 1100, 20, 0.14, 7.0),
                new Parking(98, "Galeria Krakowska", "Kraków", 1500, 100, 15, 0.10, 5.0),
                new Parking(99, "Parking Centrum", "Warszawa", 800, 750, 20, 0.15, 7.5),
                new Parking(100, "Mall Gdańsk", "Gdańsk", 50, 10, 10, 0.08, 3.0),
                new Parking(101, "Plaza Wrocław", "Wrocław", 1200, 900, 15, 0.12, 6.0),
                new Parking(102, "City Park Poznań", "Poznań", 600, 450, 15, 0.10, 5.0),
                new Parking(103, "Arkadia", "Warszawa", 2000, 1800, 20, 0.15, 8.0),
                new Parking(104, "Silesia", "Katowice", 1800, 200, 15, 0.10, 5.5),
                new Parking(105, "Złote Tarasy", "Warszawa", 900, 800, 15, 0.12, 6.5),
                new Parking(106, "Manufaktura", "Łódź", 1100, 950, 20, 0.11, 5.0),
                new Parking(107, "Pasaż Grunwaldzki", "Wrocław", 750, 600, 15, 0.10, 5.0),
                new Parking(108, "CH Auchan", "Kraków", 500, 50, 10, 0.09, 4.0),
                new Parking(109, "Westfield Mokotów", "Warszawa", 1300, 1100, 20, 0.14, 7.0),
                new Parking(98, "Galeria Krakowska", "Kraków", 1500, 100, 15, 0.10, 5.0),
                new Parking(99, "Parking Centrum", "Warszawa", 800, 750, 20, 0.15, 7.5),
                new Parking(100, "Mall Gdańsk", "Gdańsk", 50, 10, 10, 0.08, 3.0),
                new Parking(101, "Plaza Wrocław", "Wrocław", 1200, 900, 15, 0.12, 6.0),
                new Parking(102, "City Park Poznań", "Poznań", 600, 450, 15, 0.10, 5.0),
                new Parking(103, "Arkadia", "Warszawa", 2000, 1800, 20, 0.15, 8.0),
                new Parking(104, "Silesia", "Katowice", 1800, 200, 15, 0.10, 5.5),
                new Parking(105, "Złote Tarasy", "Warszawa", 900, 800, 15, 0.12, 6.5),
                new Parking(106, "Manufaktura", "Łódź", 1100, 950, 20, 0.11, 5.0),
                new Parking(107, "Pasaż Grunwaldzki", "Wrocław", 750, 600, 15, 0.10, 5.0),
                new Parking(108, "CH Auchan", "Kraków", 500, 50, 10, 0.09, 4.0),
                new Parking(109, "Westfield Mokotów", "Warszawa", 1300, 1100, 20, 0.14, 7.0),
                new Parking(98, "Galeria Krakowska", "Kraków", 1500, 100, 15, 0.10, 5.0),
                new Parking(99, "Parking Centrum", "Warszawa", 800, 750, 20, 0.15, 7.5),
                new Parking(100, "Mall Gdańsk", "Gdańsk", 50, 10, 10, 0.08, 3.0),
                new Parking(101, "Plaza Wrocław", "Wrocław", 1200, 900, 15, 0.12, 6.0),
                new Parking(102, "City Park Poznań", "Poznań", 600, 450, 15, 0.10, 5.0),
                new Parking(103, "Arkadia", "Warszawa", 2000, 1800, 20, 0.15, 8.0),
                new Parking(104, "Silesia", "Katowice", 1800, 200, 15, 0.10, 5.5),
                new Parking(105, "Złote Tarasy", "Warszawa", 900, 800, 15, 0.12, 6.5),
                new Parking(106, "Manufaktura", "Łódź", 1100, 950, 20, 0.11, 5.0),
                new Parking(107, "Pasaż Grunwaldzki", "Wrocław", 750, 600, 15, 0.10, 5.0),
                new Parking(108, "CH Auchan", "Kraków", 500, 50, 10, 0.09, 4.0),
                new Parking(109, "Westfield Mokotów", "Warszawa", 1300, 1100, 20, 0.14, 7.0)
        );

        filteredParkings = new ArrayList<>(allParkings);

        totalPages = (int) Math.ceil((double) filteredParkings.size() / itemsPerPage);

        showPage(0);
    }

    /**
     * Filtruje parkingi na podstawie wyszukiwania
     */
    private void filterParkings(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            // Pokaż wszystkie
            filteredParkings = new ArrayList<>(allParkings);
        } else {
            // Filtruj po ID, nazwie lub adresie
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
        double gap = 20;     // hgap
        double cardWidth = (containerWidth -  (gap * 2)) / 3.0;

        cardWidth = cardWidth - 5;


        for (int i = start; i < end; i++) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/parkflow/deskoptworker/components/parkingItem.fxml"));
                VBox card = loader.load();

                ParkingItemController controller = loader.getController();
                controller.setData(allParkings.get(i), currentUserRole);
                card.setMinWidth(cardWidth);
                card.setPrefWidth(cardWidth);
                card.setMaxWidth(cardWidth);


                parkingsContainer.getChildren().add(card);
            } catch (IOException e) {
                System.err.println("Couldnt find component parkingItem.fxml");
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
            // Na początku: 1 2 3 4 5 ... 10
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
            // Na końcu: 1 ... 6 7 8 9 10
            dotsContainer.getChildren().add(createPageNumber(0));
            dotsContainer.getChildren().add(createEllipsis());

            startPage = totalPages - 5;
            endPage = totalPages - 1;

            for (int i = startPage; i <= endPage; i++) {
                dotsContainer.getChildren().add(createDot(i));
            }

        } else {
            // W środku: 1 ... 4 5 6 ... 10
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
}
