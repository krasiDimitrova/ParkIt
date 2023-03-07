package uni.fmi.parkit.server.auth;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import uni.fmi.parkit.server.ErrorMessageTemplate;
import uni.fmi.parkit.server.models.ParkItUser;
import uni.fmi.parkit.server.services.UserService;

import java.util.Optional;

@Component
public class JwtTokenValidatorImpl implements JwtTokenValidator {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenValidatorImpl.class);
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String UNAUTHORIZED = "Unauthorized";
    @Value("${jwt.signing.key}")
    private String jwtSecret;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String resolveToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            throw new AuthenticationException(ErrorMessageTemplate.AUTHORIZATION_HEADER_MISSING.getMessage());
        }

        if (!authorizationHeader.startsWith(TOKEN_PREFIX)) {
            logger.debug(ErrorMessageTemplate.AUTHORIZATION_HEADER_STARTS_WITH_BEARER.getMessage());
            throw new AuthenticationException(ErrorMessageTemplate.AUTHORIZATION_HEADER_STARTS_WITH_BEARER.getMessage());
        }

        return authorizationHeader.substring(TOKEN_PREFIX.length());
    }

    @Override
    public Authentication getAuthentication(String token) {
        logger.debug("Validating auth token");

        JwtClaims claims = parseJwtClaims(token);

        Optional<ParkItUser> foundUser = userService.getByEmail(getClaim(claims, "sub"));
        return new UsernamePasswordAuthenticationToken(foundUser.get(), null, null);
    }

    private JwtClaims parseJwtClaims(String token) {
        AuthenticationException authenticationException;

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKey(new HmacKey(jwtSecret.getBytes()))
                .setRequireExpirationTime()
                .build();

        try {
            return jwtConsumer.processToClaims(token);
        } catch (InvalidJwtException e) {
            if (e.hasExpired()) {
                logger.warn("Expired JWT token: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                authenticationException = new AuthenticationException(ErrorMessageTemplate.JWT_EXPIRED.getMessage(), e);
            } else if (e.hasErrorCode(ErrorCodes.SIGNATURE_INVALID) || e.hasErrorCode(ErrorCodes.SIGNATURE_MISSING)) {
                logger.warn("JWT token has wrong signature: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                authenticationException = new AuthenticationException(ErrorMessageTemplate.JWT_WRONG_SIGNATURE.getMessage(), e);
            } else {
                logger.warn("JWT validation has failed: {} - {}", e.getClass().getSimpleName(), e.getMessage());
                authenticationException = new AuthenticationException(ErrorMessageTemplate.JWT_VALIDATION_FAILED.getMessage(), e);
            }
        } catch (RuntimeException e) {
            logger.error("Unexpected error occurred in JwtTokenValidator", e);
            authenticationException = new AuthenticationException(UNAUTHORIZED, e);
        }

        throw authenticationException;
    }

    private String getClaim(JwtClaims claims, String claimName) {
        try {
            return claims.getClaimValue(claimName, String.class);
        } catch (MalformedClaimException ex) {
            logger.warn("JWT claim {} is malformed: {} - {}", claimName, ex.getClass().getSimpleName(), ex.getMessage());
            throw new AuthenticationException(ErrorMessageTemplate.JWT_MALFORMED_CLAIM.getFormattedMessage(claimName), ex);
        }
    }
}

