package parkflow.deskoptworker.api;

import parkflow.deskoptworker.models.UserRole;
import parkflow.deskoptworker.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PaymentService - Frontend service for financial reports
 * Communicates with admin-service financial endpoints
 */
public class PaymentService {

    private final ApiClient api = new ApiClient();

    /**
     * Get financial summary
     * Returns metrics like total revenue, parking usage, pending payments, etc.
     *
     * @param timePeriod - "Today", "This Week", "This Month", etc.
     * @param parkingName - parking name (or "All parkings")
     * @return Map with financial metrics
     */
    public Map<String, Object> getFinancialSummary(String timePeriod, String parkingName) {
        try {
            // Convert UI time period to backend format
            String period = convertTimePeriodToBackend(timePeriod);

            // Build path with query params
            String path = "/admin/reports/financial/summary?period=" + period;

            // If specific parking is selected, we need to get its ID
            // For now, we'll handle "All parkings" case
            // TODO: Add parking name -> ID mapping if needed

            if (parkingName != null && !parkingName.equals("All parkings")) {
                // For specific parking, you would need to:
                // 1. Get parking ID by name from ParkingService
                // 2. Add parkingId parameter
                // path += "&parkingId=" + parkingId;
                System.out.println("Specific parking filter not yet implemented: " + parkingName);
            }

            Map<String, Object> response = api.get(path, true, Map.class);

            return response != null ? response : createEmptySummary();

        } catch (Exception e) {
            System.err.println("Failed to get financial summary: " + e.getMessage());
            return createEmptySummary();
        }
    }

    /**
     * Get revenue over time (for bar chart)
     * Returns array of data points with date/period and revenue
     *
     * @param timePeriod - "This Week", "This Month", etc.
     * @param parkingName - parking name (or "All parkings")
     * @return List of revenue data points
     */
    public List<Map<String, Object>> getRevenueOverTime(String timePeriod, String parkingName) {
        try {
            String period = convertTimePeriodToBackend(timePeriod);

            String path = "/admin/reports/financial/revenue-over-time?period=" + period;

            if (parkingName != null && !parkingName.equals("All parkings")) {
                // TODO: Add parkingId parameter
                System.out.println("Specific parking filter not yet implemented: " + parkingName);
            }

            List<Map<String, Object>> response = api.get(path, true, List.class);

            return response != null ? response : new ArrayList<>();

        } catch (Exception e) {
            System.err.println("Failed to get revenue over time: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get revenue distribution by parking (for pie chart)
     * Only for ADMIN role
     *
     * @param timePeriod - "This Week", "This Month", etc.
     * @return List of parking revenue breakdowns
     */
    public List<Map<String, Object>> getRevenueDistribution(String timePeriod) {
        try {
            // Check if user is admin
            UserRole role = SessionManager.getInstance().getCurrentUser().getRole();
            if (role != UserRole.ADMIN) {
                System.out.println("Revenue distribution only available for admins");
                return new ArrayList<>();
            }

            String period = convertTimePeriodToBackend(timePeriod);

            String path = "/admin/reports/financial/revenue-distribution?period=" + period;

            List<Map<String, Object>> response = api.get(path, true, List.class);

            return response != null ? response : new ArrayList<>();

        } catch (Exception e) {
            System.err.println("Failed to get revenue distribution: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get detailed transaction list
     *
     * @param timePeriod - "Today", "This Week", etc.
     * @param parkingName - parking name (or "All parkings")
     * @param status - "all", "paid", "pending", "failed"
     * @return List of transactions
     */
    public List<Map<String, Object>> getTransactions(String timePeriod, String parkingName, String status) {
        try {
            String period = convertTimePeriodToBackend(timePeriod);

            String path = "/admin/reports/financial/transactions?period=" + period + "&status=" + status;

            if (parkingName != null && !parkingName.equals("All parkings")) {
                // TODO: Add parkingId parameter
                System.out.println("Specific parking filter not yet implemented: " + parkingName);
            }

            List<Map<String, Object>> response = api.get(path, true, List.class);

            return response != null ? response : new ArrayList<>();

        } catch (Exception e) {
            System.err.println("Failed to get transactions: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Convert UI time period to backend format
     *
     * UI Format: "Today", "Yesterday", "This Week", "Last Week", "This Month", "Last Month", "This Quarter", "This Year"
     * Backend Format: "today", "week", "month", "quarter", "year"
     */
    private String convertTimePeriodToBackend(String uiPeriod) {
        if (uiPeriod == null) {
            return "month";
        }

        return switch (uiPeriod.toLowerCase()) {
            case "today" -> "today";
            case "yesterday" -> "today"; // Backend doesn't have "yesterday", use "today"
            case "this week", "last week" -> "week";
            case "this month", "last month" -> "month";
            case "this quarter" -> "quarter";
            case "this year" -> "year";
            default -> "month";
        };
    }

    /**
     * Create empty summary (fallback when API fails)
     */
    private Map<String, Object> createEmptySummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRevenue", 0.0);
        summary.put("parkingUsage", 0.0);
        summary.put("pendingPayments", 0.0);
        summary.put("reservationFees", 0.0);
        summary.put("totalTransactions", 0);
        summary.put("avgTransactionValue", 0.0);
        summary.put("revenueGrowth", 0.0);
        return summary;
    }

    /**
     * Helper to safely get Double value from Map
     */
    public static Double getDoubleValue(Map<String, Object> map, String key) {
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

    /**
     * Helper to safely get Integer value from Map
     */
    public static Integer getIntValue(Map<String, Object> map, String key) {
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

    /**
     * Helper to safely get String value from Map
     */
    public static String getStringValue(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) {
            return "";
        }

        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
}