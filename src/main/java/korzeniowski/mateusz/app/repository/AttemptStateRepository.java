package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.test.AttemptState;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface AttemptStateRepository extends JpaRepository<AttemptState, Long> {
    Optional<AttemptState> findByAttemptId(Long attemptId);
}
