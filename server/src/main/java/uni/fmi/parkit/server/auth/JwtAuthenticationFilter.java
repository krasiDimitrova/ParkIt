package uni.fmi.parkit.server.auth;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import uni.fmi.parkit.server.models.ParkItUser;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String AUTH_LOGIN_URL = "/login";
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_ISSUER = "parkit-api";
    private static final Long JWT_TOKEN_EXPIRES_TIME = 60 * 60 * 2L; // 2 hours token lifetime
    private final AuthenticationManager authenticationManager;
    private final String jwtSecret;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String jwtSecret) {
        this.authenticationManager = authenticationManager;
        this.jwtSecret = jwtSecret;
        setFilterProcessesUrl(AUTH_LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("email");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) {
        ParkItUser user = ((ParkItUser) authentication.getPrincipal());

        JwtClaims claims = new JwtClaims();
        claims.setSubject(user.getEmail());
        claims.setIssuer(TOKEN_ISSUER);
        claims.setIssuedAtToNow();
        claims.setExpirationTime(NumericDate.fromSeconds(NumericDate.now().getValue() + JWT_TOKEN_EXPIRES_TIME));

        JsonWebSignature jws = new JsonWebSignature();
        jws.setDoKeyValidation(false);
        jws.setPayload(claims.toJson());
        jws.setKey(new HmacKey(jwtSecret.getBytes()));
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);

        try {
            String token = jws.getCompactSerialization();
            response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + token);
        } catch (JoseException ex) {
            throw new AuthenticationException("Failed token issuing", ex);
        }
    }
}

