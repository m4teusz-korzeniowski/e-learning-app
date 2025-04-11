package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.test.Result;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ResultRepository extends CrudRepository<Result, Long> {

    @Query("SELECT result FROM Result result WHERE result.user.id = :userId AND result.test.id = :testId")
    public Optional<Result> findByUserId(@Param("userId") long userId, @Param("testId") long testId);
}
