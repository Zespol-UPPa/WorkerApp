package parkflow.deskoptworker.api;

public record DecodedToken(
        String accountId,
        String role,
        long exp
) {}
