package korzeniowski.mateusz.app.model.user.dto;

import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserRole;

import java.util.stream.Stream;

public class UserDisplayDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String group;

    public UserDisplayDto(Long id, String firstName, String lastName, String email, String role, String group) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public static UserDisplayDto map(User user) {
        Stream<String> stream = user.getUserRoles().stream().map(UserRole::getName);
        String role = stream.toList().get(0);
        if (user.getGroup() != null) {
            return new UserDisplayDto(user.getId(), user.getFirstName(),
                    user.getLastName(), user.getEmail(), role, user.getGroup().getName());
        } else {
            return new UserDisplayDto(user.getId(), user.getFirstName(),
                    user.getLastName(), user.getEmail(), role, "Brak");
        }
    }
}
