package uni.fmi.parkit.server.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static uni.fmi.parkit.server.controllers.ParkingSpaceController.PARKING_SPACE_ONBOARDING_ENDPOINT;
import static uni.fmi.parkit.server.controllers.ParkingSpaceController.PARKING_SPACE_STATUS_UPDATE_ENDPOINT;
import static uni.fmi.parkit.server.controllers.UserController.REGISTRATION_ENDPOINT;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private JwtTokenValidator jwtTokenValidator;

    private AuthenticationFacade authenticationFacade;

    @Autowired
    public void setJwtTokenValidator(JwtTokenValidator jwtTokenValidator) {
        this.jwtTokenValidator = jwtTokenValidator;
    }

    @Autowired
    public void setAuthenticationFacade(AuthenticationFacade authenticationFacade) {
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        logger.debug("Processing auth filter chain for token");

        String requestURI = req.getRequestURI();
        if (REGISTRATION_ENDPOINT.equals(requestURI) ||
                PARKING_SPACE_STATUS_UPDATE_ENDPOINT.equals(requestURI) ||
                PARKING_SPACE_ONBOARDING_ENDPOINT.equals(requestURI)) {
            filterChain.doFilter(req, res);
            return;
        }

        try {
            String bearerToken = req.getHeader(AUTHORIZATION_HEADER);
            String token = jwtTokenValidator.resolveToken(bearerToken);

            Authentication auth = jwtTokenValidator.getAuthentication(token);
            authenticationFacade.setAuthentication(auth);
            logger.debug("Successfully authenticated through token");

            filterChain.doFilter(req, res);
        } catch (AuthenticationException ex) {
            HttpUtils.sendApiError(res, HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
        }
    }
}
