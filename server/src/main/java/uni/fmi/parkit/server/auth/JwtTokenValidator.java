package uni.fmi.parkit.server.auth;

import org.springframework.security.core.Authentication;

/**
 * Validates the user through its token
 */
public interface JwtTokenValidator {
    /**
     * Get a jwt token from authorizationHeader
     * @param authorizationHeader
     * @return {@link String} - jwt token
     */
    String resolveToken(String authorizationHeader);

    /**
     * Validates the current token through AuthenticationService public key
     * and if successful returns an Authentication object that represent the current user
     * @param token
     * @return {@link Authentication}
     */
    Authentication getAuthentication(String token);
}
