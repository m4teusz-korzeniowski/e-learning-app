package korzeniowski.mateusz.app.model.user.dto;

import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserRole;

import java.util.List;

public class UserSettingsDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String pesel;
    private String group;
    private String role;

    public UserSettingsDto(Long id, String firstName, String lastName,
                           String email, String pesel, String group, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.pesel = pesel;
        this.group = group;
        this.role = role;
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

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public static UserSettingsDto map(User user) {
        String groupName;
        if (user.getGroup() == null) {
            groupName = "Brak";
        } else {
            groupName = user.getGroup().getName();
        }
        List<String> role = user.getUserRoles().stream().map(UserRole::getName).toList();
        return new UserSettingsDto(user.getId(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPesel(), groupName, role.get(0));
    }
}
