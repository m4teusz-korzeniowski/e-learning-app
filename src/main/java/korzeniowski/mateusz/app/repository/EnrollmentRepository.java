package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.enroll.Enrollment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EnrollmentRepository extends CrudRepository<Enrollment, Long> {
    @Query("select e from Enrollment e where e.user_id = :userId and e.course_id= :courseId")
    Optional<Enrollment> findByUserIdAndCourseId(@Param("userId") long userId, @Param("courseId") long courseId);
}
