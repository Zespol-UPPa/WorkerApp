package parkflow.deskoptworker.api;

import parkflow.deskoptworker.models.UserRole;
import parkflow.deskoptworker.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PaymentService - Frontend service for financial reports
 * Supports both ADMIN and WORKER roles with different base paths
 */
public class PaymentService {

    private final ApiClient api = new ApiClient();

    /**
     * Get financial summary
     * ADMIN: calls /admin/reports/financial/summary
     * WORKER: calls /worker/reports/financial/summary
     */
    public Map<String, Object> getFinancialSummary(String timePeriod, String parkingName) {
        try {
            String period = convertTimePeriodToBackend(timePeriod);

            // Determine base path based on role
            UserRole role = SessionManager.getInstance().getCurrentUser().getRole();
            String basePath = getBasePathForRole(role);

            String path = basePath + "/reports/financial/summary?period=" + period;

            // For ADMIN with specific parking (future enhancement)
            if (role == UserRole.ADMIN && parkingName != null && !parkingName.equals("All parkings")) {
                System.out.println("Specific parking filter for ADMIN not yet implemented: " + parkingName);
            }

            // For WORKER, parkingId is automatically determined by backend

            Map<String, Object> response = api.get(path, true, Map.class);

            return response != null ? response : createEmptySummary();

        } catch (Exception e) {
            System.err.println("Failed to get financial summary: " + e.getMessage());
            return createEmptySummary();
        }
    }

    /**
     * Get revenue over time (for bar chart)
     */
    public List<Map<String, Object>> getRevenueOverTime(String timePeriod, String parkingName) {
        try {
            String period = convertTimePeriodToBackend(timePeriod);

            UserRole role = SessionManager.getInstance().getCurrentUser().getRole();
            String basePath = getBasePathForRole(role);

            String path = basePath + "/reports/financial/revenue-over-time?period=" + period;

            if (role == UserRole.ADMIN && parkingName != null && !parkingName.equals("All parkings")) {
                System.out.println("Specific parking filter for ADMIN not yet implemented: " + parkingName);
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
     * Only for ADMIN role - workers don't have access to this
     */
    public List<Map<String, Object>> getRevenueDistribution(String timePeriod) {
        try {
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
     */
    public List<Map<String, Object>> getTransactions(String timePeriod, String parkingName, String status) {
        try {
            String period = convertTimePeriodToBackend(timePeriod);

            UserRole role = SessionManager.getInstance().getCurrentUser().getRole();
            String basePath = getBasePathForRole(role);

            String path = basePath + "/reports/financial/transactions?period=" + period + "&status=" + status;

            if (role == UserRole.ADMIN && parkingName != null && !parkingName.equals("All parkings")) {
                System.out.println("Specific parking filter for ADMIN not yet implemented: " + parkingName);
            }

            List<Map<String, Object>> response = api.get(path, true, List.class);

            return response != null ? response : new ArrayList<>();

        } catch (Exception e) {
            System.err.println("Failed to get transactions: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get base API path based on user role
     * ADMIN: /admin
     * WORKER: /worker
     */
    private String getBasePathForRole(UserRole role) {
        return switch (role) {
            case ADMIN -> "/admin";
            case WORKER -> "/worker";
            default -> "/admin"; // Fallback
        };
    }

    /**
     * Convert UI time period to backend format
     */
    private String convertTimePeriodToBackend(String uiPeriod) {
        if (uiPeriod == null) {
            return "semester"; // Default for fixed 6-month view
        }

        return switch (uiPeriod.toLowerCase()) {
            case "today" -> "today";
            case "yesterday" -> "today";
            case "this week", "last week" -> "week";
            case "this month", "last month" -> "month";
            case "this quarter" -> "quarter";
            case "semester" -> "semester"; // 6 months
            case "this year" -> "year";
            default -> "semester";
        };
    }

    /**
     * Create empty summary (fallback)
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

    // Helper methods remain the same...
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

    public static String getStringValue(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) {
            return "";
        }
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
}