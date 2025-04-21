package korzeniowski.mateusz.app.model.user.dto;

import java.util.Set;

public class UserCredentialsDto {
    private final String email;
    private final String password;
    private final Set<String> roles;
    private final Boolean enabled;

    public UserCredentialsDto(String email, String password, Set<String> roles, Boolean enabled) {
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Boolean getEnabled() {
        return enabled;
    }
}
