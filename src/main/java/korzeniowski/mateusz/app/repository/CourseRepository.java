package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllById(Long id);

    List<Course> findAllByCreatorId(Long id);

    Optional<Course> findByName(String courseName);

    @Query(value = "select course from Course course where course.creatorId = " +
            "(select user.id from User user " +
            " where upper(user.firstName) like upper(concat('%', :keyword,'%'))" +
            " or upper(user.lastName) like upper(concat('%', :keyword, '%')))" +
            " or upper(course.name) like upper(concat('%', :keyword,'%'))")
    Page<Course> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
