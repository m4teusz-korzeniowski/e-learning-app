package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.test.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

}
