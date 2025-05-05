package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.test.Attempt;
import korzeniowski.mateusz.app.model.course.test.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    boolean existsByUserIdAndTestId(Long userId, Long testId);

    @Query("select attempt from Attempt attempt where " +
            "attempt.user.id = :userId and attempt.test.id = :testId and attempt.status = :status")
    Optional<Attempt> findByStatus(@Param("userId") Long userId,
                                   @Param("testId") Long testId,
                                   @Param("status") AttemptStatus status);

    @Query("select attempt from Attempt attempt where" +
            " attempt.user.id = :userId and attempt.test.id = :testId and attempt.status = :status")
    List<Attempt> findAllByStatus(@Param("userId") Long userId,
                                  @Param("testId") Long testId,
                                  @Param("status") AttemptStatus status);

    Integer countByUserIdAndTestId(@Param("userId") Long userId, @Param("testId") Long testId);

    @Query("select attempt.id from Attempt attempt " +
            "where attempt.user.id = :userId and attempt.test.id = :testId" +
            " and attempt.status = 'IN_PROGRESS'")
    Optional<Long> findByUserIdAndTestId(@Param("userId") Long userId, @Param("testId") Long testId);

    @Query("select case when count(attempt) > 0 then false else true end" +
            " from Attempt attempt" +
            " join attempt.test test" +
            " join test.module module" +
            " join attempt.user user" +
            " where attempt.id = :attemptId and user.id = :userId and module.visible = true")
    Boolean hasUserAccessToAttempt(@Param("attemptId") Long attemptId, @Param("userId") Long userId);


    List<Attempt> findAllByUserIdAndTestId(Long userId, Long testId);

    @Query("select attempt.answersGivenJson from Attempt attempt" +
            " where attempt.user.id = :userId and attempt.test.id = :testId")
    Optional<String> findJsonByUserIdAndTestId(@Param("userId") Long userId, @Param("testId") Long testId);

    @Modifying
    @Query("delete from Attempt a where a.user.id = :userId and a.test.id = :testId")
    void deleteByUserIdAndTestId(@Param("userId") Long userId, @Param("testId") Long testId);

    @Query("select case when count(attempt) > 0 then true else false end" +
            " from Attempt attempt" +
            " join attempt.test test" +
            " join test.module module" +
            " where module.id = :moduleId and attempt.status = :status")
    Boolean hasModuleActiveAttempt(@Param("moduleId") Long moduleId, @Param("status") AttemptStatus status);

    @Query("select case when count(attempt) > 0 then true else false end" +
            " from Attempt attempt" +
            " join attempt.test test" +
            " where test.id = :testId and attempt.status = :status")
    Boolean hasTestActiveAttempt(@Param("testId") Long testId, @Param("status") AttemptStatus status);

    @Query("select case when count(attempt) > 0 then true else false end" +
            " from Attempt attempt" +
            " join attempt.test test" +
            " join test.module module" +
            " join module.course course" +
            " where course.id = :courseId and attempt.status = :status")
    Boolean hasCourseActiveAttempt(@Param("courseId") Long courseId, @Param("status") AttemptStatus status);
}
