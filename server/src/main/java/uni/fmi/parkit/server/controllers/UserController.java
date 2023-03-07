package uni.fmi.parkit.server.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uni.fmi.parkit.server.View;
import uni.fmi.parkit.server.models.ParkItUser;
import uni.fmi.parkit.server.services.UserRegistrationSpec;
import uni.fmi.parkit.server.services.UserService;

@RestController
public class UserController {

    public static final String REGISTRATION_ENDPOINT = "/register";

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostMapping( REGISTRATION_ENDPOINT)
    @ResponseStatus(HttpStatus.OK)
    @JsonView(View.PublicParkItUserView.class)
    public ParkItUser register(@RequestBody UserRegistrationSpec userRegistrationSpec) {
        return userService.register(userRegistrationSpec);
    }
}
