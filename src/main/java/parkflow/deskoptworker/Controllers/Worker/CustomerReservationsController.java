package parkflow.deskoptworker.Controllers.Worker;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import parkflow.deskoptworker.Controllers.Components.FilterBarReservationsController;
import parkflow.deskoptworker.Controllers.Components.ReservationComponentController;
import parkflow.deskoptworker.models.Customer;
import parkflow.deskoptworker.models.Parking;
import parkflow.deskoptworker.models.Reservation;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerReservationsController {

    @FXML private HBox clearFilterBox;
    @FXML private Label clearFilterLabel;
    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;

    // Injected filter bar controller (from fx:include)
    @FXML private FilterBarReservationsController filterBarController;

    @FXML private VBox reservationsContainer;
    @FXML private VBox emptyState;
    @FXML private Label emptyStateTitle;
    @FXML private Label emptyStateSubtitle;

    // Data
    private List<Reservation> allReservations = new ArrayList<>();
    private List<Reservation> filteredReservations = new ArrayList<>();

    /**
     * -- SETTER --
     *  Sets parent controller (CustomersController)
     */
    // Parent controller reference
    @Setter
    private CustomersController parentController;

    // Filter state
    @Getter
    private String currentStatusFilter = "all";

    @Getter
    private Customer filteredCustomer = null;

    @Getter
    private Parking filteredParking = null;


    @FXML
    public void initialize() {
        System.out.println("ReservationsViewController initialized");

        // Setup filter bar listener
        if (filterBarController != null) {
            filterBarController.setListener(this::onFilterChanged);
            System.out.println("FilterBar listener connected!");
        } else {
            System.err.println("WARNING: filterBarController is null!");
        }

        loadMockData();
        applyFilters();
    }

    /**
     * Called when filter bar selection changes
     */
    private void onFilterChanged(String filter) {
        currentStatusFilter = filter;
        applyFilters();
    }

    // ==================== FILTER BY CUSTOMER ====================

    /**
     * Filters reservations by customer
     */
    public void setCustomerFilter(Customer customer) {
        this.filteredCustomer = customer;
        this.filteredParking = null;

        showClearFilterBox(true, "customer");
        titleLabel.setText(customer.getFullName() + "'s Reservations");
        subtitleLabel.setText("Viewing reservations for this customer");

        // Reset status filter to "all"
        currentStatusFilter = "all";
        if (filterBarController != null) {
            filterBarController.setFilter("all");
        }

        applyFilters();
    }

    /**
     * Filters reservations by parking
     */
    public void setParkingFilter(Parking parking) {
        this.filteredParking = parking;
        this.filteredCustomer = null;

        showClearFilterBox(true, "parking");
        titleLabel.setText(parking.getName() + " Reservations");
        subtitleLabel.setText("Viewing reservations for this parking location");

        currentStatusFilter = "all";
        if (filterBarController != null) {
            filterBarController.setFilter("all");
        }

        applyFilters();
    }

    /**
     * Clears all entity filters (customer/parking)
     */
    public void clearEntityFilter() {
        this.filteredCustomer = null;
        this.filteredParking = null;

        showClearFilterBox(false, null);
        titleLabel.setText("All Reservations");
        subtitleLabel.setText("Manage and monitor parking reservations");
        applyFilters();
    }
    private void showClearFilterBox(boolean show, String filterType) {
        clearFilterBox.setVisible(show);
        clearFilterBox.setManaged(show);

        // Aktualizuj tekst przycisku w zależności od typu filtra
        if (show && clearFilterLabel != null) {
            if ("customer".equals(filterType)) {
                clearFilterLabel.setText("Clear customer filter");
            } else if ("parking".equals(filterType)) {
                clearFilterLabel.setText("Clear parking filter");
            }
        }
    }

    // ==================== HANDLERS ====================

    @FXML
    private void onClearFilter() {

        // Wyczyść filtry
        clearEntityFilter();

        // Powiadom parent controller
        if (parentController != null) {
            parentController.showAllReservations();
        }
    }

    // ==================== FILTERING LOGIC ====================

    private void applyFilters() {
        filteredReservations = allReservations.stream()
                .filter(this::matchesCustomerFilter)
                .filter(this::matchesParkingFilter)
                .filter(this::matchesStatusFilter)
                .collect(Collectors.toList());

        displayReservations();
    }

    private boolean matchesCustomerFilter(Reservation reservation) {
        if (filteredCustomer == null) return true;
        return reservation.getRefAccountId() == filteredCustomer.getCustomerId();
    }

    private boolean matchesParkingFilter(Reservation reservation) {
        if (filteredParking == null) return true;
        return reservation.getParkingId() == filteredParking.getId();
    }

    private boolean matchesStatusFilter(Reservation reservation) {
        if ("all".equals(currentStatusFilter)) return true;

        String status = reservation.getStatusReservation();
        if (status == null) status = "upcoming";

        return switch (currentStatusFilter) {
            case "active" -> "active".equalsIgnoreCase(status);
            case "upcoming" -> "upcoming".equalsIgnoreCase(status);
            case "history" -> "completed".equalsIgnoreCase(status);
            default -> true;
        };
    }

    // ==================== DISPLAY ====================

    private void displayReservations() {
        reservationsContainer.getChildren().clear();

        if (filteredReservations.isEmpty()) {
            showEmptyState(true);
            return;
        }

        showEmptyState(false);

        for (Reservation reservation : filteredReservations) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/parkflow/deskoptworker/components/ReservationCompo.fxml")
                );
                HBox reservationCard = loader.load();

                ReservationComponentController controller = loader.getController();
                controller.setReservation(reservation);

                reservationsContainer.getChildren().add(reservationCard);

            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to load ReservationCompo.fxml");
            }
        }
    }

    private void showEmptyState(boolean show) {
        emptyState.setVisible(show);
        emptyState.setManaged(show);
        reservationsContainer.setVisible(!show);
        reservationsContainer.setManaged(!show);

        if (show) {
            String statusText = currentStatusFilter.equals("all") ? "" : currentStatusFilter + " ";

            if (filteredCustomer != null) {
                emptyStateTitle.setText("No reservations for this customer");
                emptyStateSubtitle.setText(filteredCustomer.getFullName() + " has no " + statusText + "reservations");
            } else if (filteredParking != null) {
                emptyStateTitle.setText("No reservations for this parking");
                emptyStateSubtitle.setText(filteredParking.getName() + " has no " + statusText + "reservations");
            } else {
                emptyStateTitle.setText("No reservations found");
                emptyStateSubtitle.setText("There are no " + statusText + "reservations");
            }
        }
    }

    // ==================== DATA LOADING ====================

    /**
     * Sets reservations data from external source
     */
    public void setReservations(List<Reservation> reservations) {
        this.allReservations = reservations != null ? reservations : new ArrayList<>();
        applyFilters();
    }

    /**
     * Refreshes reservations - TODO: implement API call
     */
    public void refreshReservations() {
        // TODO: Load from API/database
        loadMockData();
        applyFilters();
    }

    private void loadMockData() {
        allReservations = new ArrayList<>();

        // Mock reservation 1 - Upcoming
        Reservation r1 = new Reservation();
        r1.setReservationId(1);
        r1.setRefAccountId(1);
        r1.setCustomerName("Jan Kowalski");
        r1.setLicensePlate("KR 12345");
        r1.setParkingId(98);
        r1.setParkingSpotCode("R-23");
        r1.setParkingLocationName("Galeria Krakowska");
        r1.setValidUntil(LocalDateTime.of(2025, 11, 24, 14, 0));
        r1.setReservationFee(5.00);
        r1.setStatusReservation("upcoming");
        allReservations.add(r1);

        // Mock reservation 2 - Active
        Reservation r2 = new Reservation();
        r2.setReservationId(2);
        r2.setRefAccountId(2);
        r2.setCustomerName("Anna Nowak");
        r2.setLicensePlate("KR 12345");
        r2.setParkingId(98);
        r2.setParkingSpotCode("R-23");
        r2.setParkingLocationName("Galeria Krakowska");
        r2.setValidUntil(LocalDateTime.of(2025, 11, 24, 16, 30));
        r2.setReservationFee(12.00);
        r2.setStatusReservation("active");
        allReservations.add(r2);

        // Mock reservation 3 - Completed
        Reservation r3 = new Reservation();
        r3.setReservationId(3);
        r3.setRefAccountId(1);
        r3.setCustomerName("Jan Kowalski");
        r3.setLicensePlate("KR 12345");
        r3.setParkingId(2);
        r3.setParkingSpotCode("R-23");
        r3.setParkingLocationName("Downtown Plaza");
        r3.setValidUntil(LocalDateTime.of(2025, 11, 24, 10, 0));
        r3.setReservationFee(5.00);
        r3.setStatusReservation("completed");
        allReservations.add(r3);

        // Mock reservation 3 - Completed
        Reservation r5 = new Reservation();
        r5.setReservationId(3);
        r5.setRefAccountId(1);
        r5.setCustomerName("Jan Kowalski");
        r5.setLicensePlate("KR 12345");
        r5.setParkingId(2);
        r5.setParkingSpotCode("R-23");
        r5.setParkingLocationName("Downtown Plaza");
        r5.setValidUntil(LocalDateTime.of(2025, 11, 24, 10, 0));
        r5.setReservationFee(5.00);
        r5.setStatusReservation("completed");
        allReservations.add(r5);

        // Mock reservation 3 - Completed
        Reservation r4 = new Reservation();
        r4.setReservationId(3);
        r4.setRefAccountId(1);
        r4.setCustomerName("Jan Kowalski");
        r4.setLicensePlate("KR 12345");
        r4.setParkingId(2);
        r4.setParkingSpotCode("R-23");
        r4.setParkingLocationName("Downtown Plaza");
        r4.setValidUntil(LocalDateTime.of(2025, 11, 24, 10, 0));
        r4.setReservationFee(5.00);
        r4.setStatusReservation("completed");
        allReservations.add(r4); 



        System.out.println("Loaded " + allReservations.size() + " mock reservations");
    }
}