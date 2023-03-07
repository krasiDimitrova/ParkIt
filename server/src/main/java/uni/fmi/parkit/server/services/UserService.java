package uni.fmi.parkit.server.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import uni.fmi.parkit.server.models.ParkItUser;

import javax.validation.Valid;
import java.util.Optional;

@Validated
public interface UserService extends UserDetailsService {

    ParkItUser register(@Valid UserRegistrationSpec userRegistrationSpec);

    ParkItUser getCurrentUser();

    Optional<ParkItUser> getByEmail(String email);
}
