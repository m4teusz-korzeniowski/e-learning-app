package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.token.PasswordToken;
import korzeniowski.mateusz.app.model.token.TokenExpiryDateDto;
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
        passwordTokenRepository.findByUserId(user.getId()).ifPresent(passwordToken -> {
            token.setId(passwordToken.getId());
        });
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(60 * 24 * 28));
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

    public Optional<TokenExpiryDateDto> findTokenExpiryDateByUserId(Long userId) {
        return passwordTokenRepository.findByUserId(userId).map(TokenExpiryDateDto::map);
    }

    public boolean tokenExists(Long userId) {
        return passwordTokenRepository.findByUserId(userId).isPresent();
    }
}
