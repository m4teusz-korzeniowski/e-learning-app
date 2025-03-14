package korzeniowski.mateusz.app.config;

import korzeniowski.mateusz.app.model.user.UserService;
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
        Optional<UserCredentialsDto> credentialsByEmail = userService.findCredentialsByEmail(username);
        if (credentialsByEmail.isPresent()) {
            return User.builder()
                    .username(credentialsByEmail.get().getEmail())
                    .password(credentialsByEmail.get().getPassword())
                    .roles(credentialsByEmail.get().getRoles().toArray(String[]::new))
                    .build();
        }else {
            throw new UsernameNotFoundException(String.format("Username with email %s not found", username));
        }

    }

}
