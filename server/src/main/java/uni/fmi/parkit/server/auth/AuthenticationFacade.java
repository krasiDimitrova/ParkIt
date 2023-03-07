package uni.fmi.parkit.server.auth;

import org.springframework.security.core.Authentication;

/**
 * Abstraction of how we can get and set currently authenticated user
 */
public interface AuthenticationFacade {
    /**
     * Get currently authenticated user
     * @return {@link Authentication}
     */
    Authentication getAuthentication();

    /**
     * Set currently authenticated user
     * @param authentication
     */
    void setAuthentication(Authentication authentication);
}
