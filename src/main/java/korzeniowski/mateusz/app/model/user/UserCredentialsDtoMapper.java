package korzeniowski.mateusz.app.model.user;

import korzeniowski.mateusz.app.model.user.dto.UserCredentialsDto;

import java.util.Set;
import java.util.stream.Collectors;

public class UserCredentialsDtoMapper {
    public static UserCredentialsDto map(User user){
        String email = user.getEmail();
        String password = user.getPassword();
        Set<String> roles = user.getUserRoles().stream().map(UserRole::getName).collect(Collectors.toSet());
        return new UserCredentialsDto(email, password, roles);
    }
}
