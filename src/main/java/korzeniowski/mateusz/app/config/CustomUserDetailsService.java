package korzeniowski.mateusz.app.config;

import korzeniowski.mateusz.app.exceptions.UserDisabledException;
import korzeniowski.mateusz.app.service.UserService;
import korzeniowski.mateusz.app.model.user.dto.UserCredentialsDto;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserCredentialsDto> credentials = userService.findCredentialsByEmail(username);
        if (credentials.isPresent()) {
            if (!credentials.get().getEnabled() || credentials.get().getPassword() == null) {
                throw new UserDisabledException("*konto użytkownika jest nieaktywne!");
            }
            return User.builder()
                    .username(credentials.get().getEmail())
                    .password(credentials.get().getPassword())
                    .roles(credentials.get().getRoles().toArray(String[]::new))
                    .build();
        } else {
            throw new UsernameNotFoundException(String.format("Username with email %s not found", username));
        }

    }

}
