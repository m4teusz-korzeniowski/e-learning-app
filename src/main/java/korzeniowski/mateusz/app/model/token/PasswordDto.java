package korzeniowski.mateusz.app.model.token;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordDto {
    @NotBlank(message = "*pole nie może być puste!")
    @Size(min = 8, message = "*hasło musi składać się z co najmniej 8 znaków!")
    @Size(max = 100, message = "*hasło może zawierać maksymalnie 100 znaków!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,100}$", message = "*hasło musi zawierać co najmniej jedną dużą literę oraz jedną cyfrę!")
    private String password;
    @NotBlank(message = "*pole nie może być puste!")
    private String confirmedPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public @NotBlank String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(@NotBlank String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }
}
