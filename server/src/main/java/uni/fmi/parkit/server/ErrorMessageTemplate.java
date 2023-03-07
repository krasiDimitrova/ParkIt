package uni.fmi.parkit.server;


public enum ErrorMessageTemplate {
    AUTHORIZATION_HEADER_MISSING("Authorization header is missing."),
    AUTHORIZATION_HEADER_STARTS_WITH_BEARER("Authorization header must start with Bearer."),
    JWT_EXPIRED("JWT token has expired."),
    JWT_WRONG_SIGNATURE("JWT token has wrong signature."),
    JWT_MISSING_PROVIDER("JWT token does not contain provider claim."),
    JWT_WRONG_PROVIDER("JWT token has invalid provider - %s."),
    JWT_VALIDATION_FAILED("JWT validation has failed."),
    JWT_MALFORMED_CLAIM("JWT claim %s is malformed."),

    ENTITY_ALREADY_EXISTS("%s with %s %s already exists.")
    ;
    private final String message;

    ErrorMessageTemplate(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(message, args);
    }

}
