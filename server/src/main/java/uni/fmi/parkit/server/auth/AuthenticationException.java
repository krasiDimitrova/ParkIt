package uni.fmi.parkit.server.auth;

/**
 * Exception that is thrown when authentication has failed
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
