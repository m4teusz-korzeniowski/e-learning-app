package korzeniowski.mateusz.app.model.user;

import korzeniowski.mateusz.app.model.user.dto.UserCredentialsDto;
import korzeniowski.mateusz.app.model.user.dto.UserNameDto;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email).map(UserCredentialsDtoMapper::map);
    }

    public String findUserNameById(Long id) {
        Optional<UserNameDto> byId = userRepository.findById(id).map(UserNameDto::map);
        if (byId.isPresent()) {
            return byId.get().getFirstName() + " " + byId.get().getLastName();
        } else {
            throw new UsernameNotFoundException(String.format("Username with id %s not found", id));
        }
    }
}
