package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.Course;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Long> {
    List<Course> findAllById(Long id);
    List<Course> findAllByCreatorId(Long id);
    Optional<Course> findByName(String courseName);
}
