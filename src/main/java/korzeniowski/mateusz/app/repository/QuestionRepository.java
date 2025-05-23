package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.test.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Page<Question> findAllByTestId(@Param("testId") Long testId, Pageable pageable);

    @Query(value = "select q from Question q where q.test.id = :testId and" +
            "(upper(q.description) like upper(concat('%', :keyword, '%')) or" +
            " upper(q.questionType) like upper(concat('%', :keyword, '%')) or" +
            " upper(coalesce(q.category,'Brak')) like upper(concat('%', :keyword, '%')))",
            countQuery = "select count(q) from Question q where q.test.id = :testId and" +
                    "(upper(q.description) like upper(concat('%', :keyword, '%')) or" +
                    " upper(q.questionType) like upper(concat('%', :keyword, '%')) or" +
                    " upper(coalesce(q.category,'Brak')) like upper(concat('%', :keyword, '%')))")
    Page<Question> findAllByKeywordAndTestId(@Param("keyword") String keyword, @Param("testId") Long testId, Pageable pageable);
}
