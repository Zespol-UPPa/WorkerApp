package parkflow.deskoptworker.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import parkflow.deskoptworker.api.DecodedToken;

import java.util.Base64;

public class JwtDecoder {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static DecodedToken decode(String token) {
        try {
            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(token.split("\\.")[1])
            );

            JsonNode payload = mapper.readTree(payloadJson);

            return new DecodedToken(
                    payload.get("sub").asText(),
                    payload.get("role").asText(),
                    payload.get("exp").asLong()
            );

        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}
