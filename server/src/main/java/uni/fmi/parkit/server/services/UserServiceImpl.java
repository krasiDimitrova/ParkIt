package uni.fmi.parkit.server.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uni.fmi.parkit.server.ErrorMessageTemplate;
import uni.fmi.parkit.server.auth.AuthenticationFacade;
import uni.fmi.parkit.server.models.ParkItUser;
import uni.fmi.parkit.server.repositories.UserRepository;

import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import java.util.Optional;

@Service
@Validated
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private AuthenticationFacade authenticationFacade;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setAuthenticationFacade(AuthenticationFacade authenticationFacade) {
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public ParkItUser register(@Valid UserRegistrationSpec userRegistrationSpec) {
        checkIfUserAlreadyExists(userRegistrationSpec.getEmail());

        ParkItUser parkitUser = new ParkItUser();
        BeanUtils.copyProperties(userRegistrationSpec, parkitUser, "password");
        String password = userRegistrationSpec.getPassword();
        parkitUser.setPassword(passwordEncoder.encode(password));
        return userRepository.saveAndFlush(parkitUser);
    }

    @Override
    public ParkItUser getCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        return principal instanceof ParkItUser ? (ParkItUser) principal : null;
    }

    @Override
    public Optional<ParkItUser> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private void checkIfUserAlreadyExists(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EntityExistsException(ErrorMessageTemplate.ENTITY_ALREADY_EXISTS
                    .getFormattedMessage(ParkItUser.class.getSimpleName(), "email", email));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s does not exist", email)));
    }
}
