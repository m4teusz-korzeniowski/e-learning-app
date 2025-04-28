package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.test.Question;
import korzeniowski.mateusz.app.model.course.test.Test;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends CrudRepository<Test, Long> {

    Optional<Test> findTestById(Long id);
    @Query("select test.questions from Test test where test.id = :testId")
    List<Question> findQuestionsByTestId(@Param("testId") Long testId);

    @Query("select case when count(test) > 0 then false else true end" +
            " from Test test" +
            " join test.module module" +
            " join module.course course" +
            " join course.users user" +
            " where test.id = :testId and user.id = :userId and module.visible = true")
    Boolean hasUserAccessToTest(@Param("testId") Long testId, @Param("userId") Long userId);
}