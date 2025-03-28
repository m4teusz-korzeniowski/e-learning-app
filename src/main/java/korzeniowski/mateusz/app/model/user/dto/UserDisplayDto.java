package korzeniowski.mateusz.app.model.user.dto;

import korzeniowski.mateusz.app.model.user.User;

public class UserDisplayDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    public UserDisplayDto(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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

    public static UserDisplayDto map(User user) {
        return new UserDisplayDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
