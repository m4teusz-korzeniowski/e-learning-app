package korzeniowski.mateusz.app.model.user.dto;

import korzeniowski.mateusz.app.model.user.User;

public class UserNameDto {
    private  final String firstName;
    private final String lastName;

    public UserNameDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public static UserNameDto map(User user) {
        return new UserNameDto(user.getFirstName(), user.getLastName());
    }
}
