package korzeniowski.mateusz.app.model.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.pl.PESEL;


public class UserRegistrationDto {
    @NotBlank(message = "pole nie może być puste")
    @Size(min = 2, max = 50, message = "pole musi mieć od 2 do 50 znaków")
    private String firstName;
    @NotBlank(message = "pole nie może być puste")
    @Size(min = 2, max = 50, message = "pole musi mieć od 2 do 50 znaków")
    private String lastName;
    @Email(message = "niepoprawny format adresu e-mail")
    @NotBlank(message = "pole nie może być puste")
    private String email;
    private Boolean enabled;
    //private String confirmPassword;
    @PESEL
    private String pesel;
    private String role;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public @PESEL String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
