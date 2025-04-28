package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.test.Question;
import korzeniowski.mateusz.app.model.course.test.Test;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends CrudRepository<Test, Long> {

    public Optional<Test> findTestById(Long id);
    @Query("select test.questions from Test test where test.id = :testId")
    List<Question> findQuestionsByTestId(@Param("testId") Long testId);
}