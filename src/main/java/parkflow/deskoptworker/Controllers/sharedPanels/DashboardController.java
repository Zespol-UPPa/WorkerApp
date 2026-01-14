package parkflow.deskoptworker.Controllers.sharedPanels;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import parkflow.deskoptworker.Controllers.Components.MetricCardController;
import parkflow.deskoptworker.api.ApiClient;
import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;
import parkflow.deskoptworker.utils.NavigationManager;
import parkflow.deskoptworker.utils.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Dashboard Controller - Simplified Version
 * Shows top 3 metrics + Quick Actions (shortcuts to Reports and Parkings/Settings)
 */
public class DashboardController {

    // === TIME LABELS ===
    @FXML private Label currentTimeLabel;
    @FXML private Label currentDateLabel;

    // === TOP 3 METRIC CARDS ===
    @FXML private MetricCardController occupancyCardController;
    @FXML private MetricCardController revenueCardController;
    @FXML private MetricCardController parkingUsageCardController;

    // === QUICK ACTION CARDS ===
    @FXML private VBox reportsCard;
    @FXML private VBox secondCard;
    @FXML private ImageView secondCardImage;
    @FXML private Label secondCardTitle;
    @FXML private Label secondCardDescription;
    @FXML private Button secondCardButton;

    private Timeline clockTimeline;
    private Timeline dataRefreshTimeline;
    private ApiClient apiClient;
    private UserRole currentUserRole;

    @FXML
    public void initialize() {
        apiClient = new ApiClient();

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserRole = currentUser.getRole();
        }

        setupTopMetricCards();
        setupQuickActionCards();

        if (currentTimeLabel != null && currentDateLabel != null) {
            startClock();
        }

        // Load initial data
        loadDashboardData();

        // Auto-refresh every 30 seconds
        startAutoRefresh();
    }

    /**
     * Setup Quick Action Cards based on user role
     */
    private void setupQuickActionCards() {
        if (currentUserRole == UserRole.WORKER) {
            // WORKER: Second card = Settings
            secondCardTitle.setText("Settings");
            secondCardDescription.setText("Manage your profile and preferences");
            secondCardButton.setText("Go to Settings");

            // Try to load settings icon (optional)
            try {
                Image settingsIcon = new Image(
                        getClass().getResourceAsStream("/parkflow/deskoptworker/images/cogwheelBig.png")
                );
                secondCardImage.setImage(settingsIcon);
            } catch (Exception e) {
                System.out.println("Settings icon not found, using default");
            }
        }
    }

    /**
     * Setup top 3 Metric Cards with loading state
     */
    private void setupTopMetricCards() {
        // Current Occupancy - Orange (loading state)
        occupancyCardController.setData(
                "Current Occupancy",
                "...",
                "Loading...",
                "/parkflow/deskoptworker/images/target.png",
                "card-orange"
        );

        // Today's Revenue - Green (loading state)
        revenueCardController.setData(
                "Today's Revenue",
                "...",
                "Loading...",
                "/parkflow/deskoptworker/images/dollar.png",
                "card-green"
        );

        // Parking Usage - Purple (loading state)
        parkingUsageCardController.setData(
                "Parking Usage",
                "...",
                "Loading...",
                "/parkflow/deskoptworker/images/group.png",
                "card-purple"
        );
    }

    // ==================== QUICK ACTIONS NAVIGATION ====================

    /**
     * Handle click on Reports card
     */
    @FXML
    private void handleReportsClick(MouseEvent event) {
        System.out.println("Reports card clicked - navigating to Reports");
        NavigationManager.getInstance().navigateToReports();
    }

    /**
     * Handle click on second card (Parkings for ADMIN, Settings for WORKER)
     */
    @FXML
    private void handleSecondCardClick(MouseEvent event) {
        if (currentUserRole == UserRole.WORKER) {
            System.out.println("Settings card clicked - navigating to Settings");
            NavigationManager.getInstance().navigateToSettings();
        } else {
            System.out.println("Parkings card clicked - navigating to Parkings");
            NavigationManager.getInstance().navigateToParkings();
        }
    }

    // ==================== DATA LOADING ====================

    /**
     * Load dashboard data from API
     */
    private void loadDashboardData() {
        // Run in background thread to avoid blocking UI
        new Thread(() -> {
            try {
                User currentUser = SessionManager.getInstance().getCurrentUser();
                if (currentUser == null) {
                    System.err.println("No user logged in");
                    return;
                }

                String role = currentUser.getRole().toString().toLowerCase();
                System.out.println("Loading dashboard data for role: " + role);

                // 1. Get parking list for this user
                List<Long> parkingIds = getParkingsList(role);
                System.out.println("Found " + parkingIds.size() + " parkings: " + parkingIds);

                // 2. Get financial summary (today's revenue)
                Map<String, Object> financialSummary = getFinancialSummary(role, "today");
                System.out.println("Financial summary received: " + financialSummary);

                // 3. Calculate occupancy across all parkings
                OccupancyData occupancy = calculateOccupancy(parkingIds, role);
                System.out.println("Occupancy calculated: " + occupancy.percent + "% (" +
                        occupancy.occupied + "/" + occupancy.total + ")");

                // 4. Update UI on JavaFX thread
                Platform.runLater(() -> {
                    updateTopMetrics(occupancy, financialSummary);
                });

            } catch (Exception e) {
                System.err.println("Failed to load dashboard data: " + e.getMessage());
                e.printStackTrace();

                Platform.runLater(() -> {
                    showErrorState();
                });
            }
        }).start();
    }

    /**
     * Get list of parking IDs based on user role
     */
    private List<Long> getParkingsList(String role) {
        try {
            List response = apiClient.get("/" + role + "/parkings", true, List.class);

            if (response == null || response.isEmpty()) {
                return List.of();
            }

            // Convert to List<Long>
            List<Long> parkingIds = new java.util.ArrayList<>();
            for (Object item : response) {
                if (item instanceof Number) {
                    parkingIds.add(((Number) item).longValue());
                } else if (item instanceof String) {
                    try {
                        parkingIds.add(Long.parseLong((String) item));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid parking ID format: " + item);
                    }
                }
            }

            return parkingIds;
        } catch (Exception e) {
            System.err.println("Failed to get parkings list: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Get financial summary
     */
    private Map<String, Object> getFinancialSummary(String role, String period) {
        try {
            Map response = apiClient.get(
                    "/" + role + "/reports/financial/summary?period=" + period,
                    true,
                    Map.class
            );
            return (Map<String, Object>) response;
        } catch (Exception e) {
            System.err.println("Failed to get financial summary: " + e.getMessage());

            // Return empty data structure
            return Map.of(
                    "totalRevenue", 0.0,
                    "parkingUsage", 0.0,
                    "totalTransactions", 0
            );
        }
    }

    /**
     * Calculate occupancy across all parkings
     */
    private OccupancyData calculateOccupancy(List<Long> parkingIds, String role) {
        int totalSpots = 0;
        int occupiedSpots = 0;

        for (Long parkingId : parkingIds) {
            try {
                Map stats = apiClient.get(
                        "/" + role + "/parkings/" + parkingId + "/stats",
                        true,
                        Map.class
                );

                if (stats != null) {
                    Integer total = getIntValue(stats.get("totalSpots"));
                    Integer occupied = getIntValue(stats.get("occupiedSpots"));

                    totalSpots += total;
                    occupiedSpots += occupied;
                }
            } catch (Exception e) {
                System.err.println("Failed to get stats for parking " + parkingId + ": " + e.getMessage());
            }
        }

        double percent = totalSpots > 0 ? (occupiedSpots * 100.0 / totalSpots) : 0.0;
        return new OccupancyData(percent, occupiedSpots, totalSpots);
    }

    /**
     * Update top metric cards with data
     */
    private void updateTopMetrics(OccupancyData occupancy, Map<String, Object> financial) {
        // Update Occupancy Card
        if (occupancy.total == 0) {
            occupancyCardController.setValue("N/A");
            occupancyCardController.setSubtitle("No parkings assigned");
        } else {
            occupancyCardController.setValue(String.format("%.0f %%", occupancy.percent));
            occupancyCardController.setSubtitle(String.format("%d/%d spots",
                    occupancy.occupied, occupancy.total));
        }

        // Update Revenue Card
        double todayRevenue = getDoubleValue(financial, "totalRevenue");
        int totalTransactions = getIntValue(financial, "totalTransactions");

        revenueCardController.setValue(String.format("%.2f PLN", todayRevenue));
        revenueCardController.setSubtitle(totalTransactions > 0
                ? String.format("%d transactions", totalTransactions)
                : "No transactions today");

        // Update Parking Usage Card
        double parkingUsage = getDoubleValue(financial, "parkingUsage");
        parkingUsageCardController.setValue(String.format("%.2f PLN", parkingUsage));
        parkingUsageCardController.setSubtitle("Today's parking revenue");
    }

    /**
     * Show error state on cards
     */
    private void showErrorState() {
        occupancyCardController.setValue("Error");
        occupancyCardController.setSubtitle("Failed to load data");

        revenueCardController.setValue("Error");
        revenueCardController.setSubtitle("Failed to load data");

        parkingUsageCardController.setValue("Error");
        parkingUsageCardController.setSubtitle("Failed to load data");
    }

    // ==================== AUTO REFRESH ====================

    /**
     * Start auto-refresh timer (every 30 seconds)
     */
    private void startAutoRefresh() {
        dataRefreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(30), event -> loadDashboardData())
        );
        dataRefreshTimeline.setCycleCount(Animation.INDEFINITE);
        dataRefreshTimeline.play();
    }

    /**
     * Stop auto-refresh (call when navigating away)
     */
    public void stopAutoRefresh() {
        if (dataRefreshTimeline != null) {
            dataRefreshTimeline.stop();
        }
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
    }

    // ==================== CLOCK ====================

    private void startClock() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.ENGLISH);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.ENGLISH);

        clockTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, _ -> {
                    LocalDateTime now = LocalDateTime.now();
                    currentTimeLabel.setText(now.format(timeFormatter));
                    currentDateLabel.setText(now.format(dateFormatter));
                }),
                new KeyFrame(Duration.seconds(1))
        );

        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    // ==================== HELPER METHODS ====================

    private double getDoubleValue(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) {
            return 0.0;
        }
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private int getIntValue(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Double) return ((Double) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    // ==================== DATA CLASSES ====================

    /**
     * Occupancy data holder
     */
    private static class OccupancyData {
        final double percent;
        final int occupied;
        final int total;

        OccupancyData(double percent, int occupied, int total) {
            this.percent = percent;
            this.occupied = occupied;
            this.total = total;
        }
    }

    private int getIntValue(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) {
            return 0;
        }
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}