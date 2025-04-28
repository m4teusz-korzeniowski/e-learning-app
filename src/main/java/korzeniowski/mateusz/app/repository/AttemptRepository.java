package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.test.Attempt;
import korzeniowski.mateusz.app.model.course.test.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    boolean existsByUserIdAndTestId(Long userId, Long testId);

    @Query("select attempt from Attempt attempt where " +
            "attempt.user.id = :userId and attempt.test.id = :testId and attempt.status = :status")
    Optional<Attempt> findByStatus(@Param("userId") Long userId,
                                   @Param("testId") Long testId,
                                   @Param("status") AttemptStatus status);

    Integer countByUserIdAndTestId(@Param("userId") Long userId, @Param("testId") Long testId);

    @Query("select attempt.id from Attempt attempt " +
            "where attempt.user.id = :userId and attempt.test.id = :testId")
    Optional<Long> findByUserIdAndTestId(@Param("userId") Long userId, @Param("testId") Long testId);

}
