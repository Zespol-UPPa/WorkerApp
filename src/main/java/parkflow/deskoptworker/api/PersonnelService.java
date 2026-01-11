package parkflow.deskoptworker.api;

import parkflow.deskoptworker.models.User;
import parkflow.deskoptworker.models.UserRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for managing company personnel (admins + workers)
 * Only accessible to ADMIN users
 */
public class PersonnelService {

    private final ApiClient api = new ApiClient();

    /**
     * Get all company personnel (admins + workers)
     * Returns combined list from admin's company
     */
    public List<User> getCompanyPersonnel() {
        try {
            List<Map<String, Object>> response = api.get(
                    "/admin/personnel",
                    true,
                    List.class
            );

            if (response == null || response.isEmpty()) {
                System.out.println("No personnel found for company");
                return new ArrayList<>();
            }

            List<User> personnel = new ArrayList<>();

            for (Object item : response) {
                if (item instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> userMap = (Map<String, Object>) item;
                    User user = parseUserFromMap(userMap);
                    if (user != null) {
                        personnel.add(user);
                    }
                }
            }

            System.out.println("Loaded " + personnel.size() + " personnel members");
            return personnel;

        } catch (Exception e) {
            System.err.println("Error loading company personnel: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Deactivate a worker account
     * Only workers can be deactivated (not admins)
     */
    public boolean deactivateWorker(int accountId) {
        try {
            String path = "/admin/personnel/worker/" + accountId + "/deactivate";

            api.put(path, null, true, Void.class);

            System.out.println("Worker " + accountId + " deactivated successfully");
            return true;

        } catch (Exception e) {
            System.err.println("Error deactivating worker: " + e.getMessage());
            return false;
        }
    }

    /**
     * Activate a worker account
     * Only workers can be activated (not admins)
     */
    public boolean activateWorker(int accountId) {
        try {
            String path = "/admin/personnel/worker/" + accountId + "/activate";

            api.put(path, null, true, Void.class);

            System.out.println("Worker " + accountId + " activated successfully");
            return true;

        } catch (Exception e) {
            System.err.println("Error activating worker: " + e.getMessage());
            return false;
        }
    }

    /**
     * Parse User object from API response map
     */
    private User parseUserFromMap(Map<String, Object> map) {
        try {
            int id = getIntValue(map.get("id"));
            String firstName = (String) map.getOrDefault("firstName", "");
            String lastName = (String) map.getOrDefault("lastName", "");
            String phoneNumber = (String) map.getOrDefault("phoneNumber", "");
            String email = (String) map.getOrDefault("email", "");
            String pesel = (String) map.getOrDefault("peselNumber", "");

            String roleStr = (String) map.get("role");
            UserRole role = UserRole.valueOf(roleStr);

            boolean active = (boolean) map.getOrDefault("active", true);

            User user = new User(id, firstName, lastName, phoneNumber, email, pesel, role, active);

            // Optional: Parse parkingName for workers
            if (map.containsKey("parkingName")) {
                String parkingName = (String) map.get("parkingName");
                // You could add parkingName to User model if needed
            }

            return user;

        } catch (Exception e) {
            System.err.println("Error parsing user from map: " + e.getMessage());
            return null;
        }
    }

    /**
     * Helper to safely convert Object to int
     */
    private int getIntValue(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Double) return ((Double) value).intValue();
        if (value instanceof Long) return ((Long) value).intValue();
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