package parkflow.deskoptworker.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActivationRequest {
    // Getters and Setters
    private String code;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String peselNumber;
    private String password;
    private String confirmPassword;

}
