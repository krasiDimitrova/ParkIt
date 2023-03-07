package uni.fmi.parkit.server.services;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class UserRegistrationSpec {
    @NotNull
    @NotEmpty
    @Length(min = 2, max = 50)
    private String firstName;

    @NotNull
    @NotEmpty
    @Length(min = 2, max = 50)
    private String lastName;

    @NotNull
    @NotEmpty
    @Length(min = 6, max = 12)
    private String password;

    @NotNull
    @NotEmpty
    @Length(min = 8, max = 250)
    private String email;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
