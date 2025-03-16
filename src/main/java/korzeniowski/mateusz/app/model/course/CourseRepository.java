package korzeniowski.mateusz.app.model.course;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CourseRepository extends CrudRepository<Course, Long> {
    List<Course> findAllById(Long id);
    List<Course> findAllByCreatorId(Long id);
}
