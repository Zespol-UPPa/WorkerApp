package parkflow.deskoptworker.api;

import parkflow.deskoptworker.dto.ActivationInfoResponse;
import parkflow.deskoptworker.dto.ActivationRequest;

import java.util.Map;

public class ActivationService {

    private final ApiClient api = new ApiClient();

    /**
     * Get activation info by code (step 1)
     */
    public ActivationInfoResponse getActivationInfo(String code) {
        try {
            // ApiClient will automatically show alert if there's an error
            return api.get(
                    "/api/auth/activation-info?code=" + code,
                    false,
                    ActivationInfoResponse.class
            );
        } catch (RuntimeException e) {

            return null;
        }
    }


    /**
     * Activate account with password and updated personal data (step 2)
     */
    public boolean activate(ActivationRequest request, String role) {
        try {
            String endpoint = "Worker".equalsIgnoreCase(role)
                    ? "/api/auth/activate/worker"
                    : "/api/auth/activate/admin";

            Map<String, Object> response = api.post(endpoint, request, false, Map.class);

            System.out.println("Server response: " + response);

            return true;
        } catch (RuntimeException e) {
            // Alert already shown by ApiClient
            System.err.println("Activation error: " + e.getMessage());
            return false;
        }
    }
}