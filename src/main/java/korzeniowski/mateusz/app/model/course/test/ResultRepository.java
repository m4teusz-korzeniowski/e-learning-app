package korzeniowski.mateusz.app.model.course.test;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ResultRepository extends CrudRepository<Result, Long> {

    @Query("SELECT result FROM Result result WHERE result.user_id = :userId AND result.test_id = :testId")
    public Optional<Result> findByUserId(@Param("userId") long userId, @Param("testId") long testId);
}
