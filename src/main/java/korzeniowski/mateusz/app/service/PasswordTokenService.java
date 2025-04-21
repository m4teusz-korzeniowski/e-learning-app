package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.token.PasswordToken;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.repository.PasswordTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordTokenService {
    private final PasswordTokenRepository passwordTokenRepository;

    public PasswordTokenService(PasswordTokenRepository passwordTokenRepository) {
        this.passwordTokenRepository = passwordTokenRepository;
    }

    public PasswordToken generatePasswordTokenForUser(User user) {
        PasswordToken token = new PasswordToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(7 * 60 * 24));
        return passwordTokenRepository.save(token);
    }

    public Optional<User> findUserByToken(String token) {
        return passwordTokenRepository.findByToken(token)
                .filter(passwordToken -> passwordToken.getExpiryDate().isAfter(LocalDateTime.now()))
                .map(PasswordToken::getUser);
    }

    public void deleteToken(String token) {
        passwordTokenRepository.findByToken(token).ifPresent(passwordTokenRepository::delete);
    }
}
