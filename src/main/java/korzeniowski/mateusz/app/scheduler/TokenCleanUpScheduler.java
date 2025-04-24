package korzeniowski.mateusz.app.scheduler;

import korzeniowski.mateusz.app.repository.PasswordTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class TokenCleanUpScheduler {

    private final PasswordTokenRepository passwordTokenRepository;

    public TokenCleanUpScheduler(PasswordTokenRepository passwordTokenRepository) {
        this.passwordTokenRepository = passwordTokenRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanUpExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        passwordTokenRepository.deleteByExpiryDateBefore(now);
    }
}
