package korzeniowski.mateusz.app.scheduler;

import korzeniowski.mateusz.app.model.course.test.AttemptState;
import korzeniowski.mateusz.app.model.course.test.AttemptStatus;
import korzeniowski.mateusz.app.model.course.test.dto.TestAttemptDto;
import korzeniowski.mateusz.app.repository.AttemptRepository;
import korzeniowski.mateusz.app.repository.AttemptStateRepository;
import korzeniowski.mateusz.app.repository.PasswordTokenRepository;
import korzeniowski.mateusz.app.service.AttemptService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CleanUpScheduler {

    private final PasswordTokenRepository passwordTokenRepository;
    private final AttemptService attemptService;
    private final AttemptRepository attemptRepository;
    private final AttemptStateRepository attemptStateRepository;

    public CleanUpScheduler(PasswordTokenRepository passwordTokenRepository, AttemptService attemptService, AttemptRepository attemptRepository, AttemptStateRepository attemptStateRepository) {
        this.passwordTokenRepository = passwordTokenRepository;
        this.attemptService = attemptService;
        this.attemptRepository = attemptRepository;
        this.attemptStateRepository = attemptStateRepository;
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void cleanUpExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        passwordTokenRepository.deleteByExpiryDateBefore(now);
    }

    @Scheduled(cron = "0 * * * * *")
    public void finalizeExpiredAttempts() {
        List<Long> attemptIdsInProgress = attemptRepository.findIdsByStatus(AttemptStatus.IN_PROGRESS);
        for (Long attemptId : attemptIdsInProgress) {
            AttemptState attemptState = attemptService.findAttemptState(attemptId);
            TestAttemptDto attempt = attemptService.loadAttempt(attemptState);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = attempt.getAttemptStartTime().plusMinutes(attempt.getDuration());
            if (now.isAfter(endTime)) {
                attemptService.finishAttempt(attemptState.getAttempt().getId(), attempt);
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void finalizeAttemptsLastModifiedWeekAgo() {
        List<AttemptState> attemptStates = attemptStateRepository.findAll();
        for (AttemptState attemptState : attemptStates) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = attemptState.getLastModified().plusHours(24*7);
            if (now.isAfter(endTime)) {
                TestAttemptDto attempt = attemptService.loadAttempt(attemptState);
                Long attemptId = attemptState.getAttempt().getId();
                attemptService.finishAttempt(attemptId, attempt);
            }
        }
    }

}
