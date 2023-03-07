package uni.fmi.parkit.server.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uni.fmi.parkit.server.controllers.ParkingSpaceController;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static uni.fmi.parkit.server.controllers.ParkingSpaceController.PARKING_SPACE_ONBOARDING_ENDPOINT;

@Component
public class SensorUpdateFilter extends OncePerRequestFilter {

    private static final String PRINCIPAL = "Sensor";

    private static final String SHARED_SECRET_HEADER = "PARKIT_PARKING_SPACE_SHARED_SECRET";

    private static final String SHARED_SECRET_VALUE = "f4255256-94a4-473b-b8c9-26b7b7da91d8";


    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = req.getRequestURI();

        if (!ParkingSpaceController.PARKING_SPACE_STATUS_UPDATE_ENDPOINT.equals(requestUri)
                && !PARKING_SPACE_ONBOARDING_ENDPOINT.equals(requestUri)) {
            filterChain.doFilter(req, res);
            return;
        }

        String sharedSecret = req.getHeader(SHARED_SECRET_HEADER);
        if (SHARED_SECRET_VALUE.equals(sharedSecret)) {
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(PRINCIPAL, null, null));
        }

        filterChain.doFilter(req, res);
    }
}
