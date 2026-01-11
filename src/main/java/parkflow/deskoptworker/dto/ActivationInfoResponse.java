package parkflow.deskoptworker.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActivationInfoResponse {
    // Getters and Setters
    private Long accountId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String peselNumber;
    private String role;
    private String parkingName; // Only for Worker
    private String companyName; // Only for Admin

}
