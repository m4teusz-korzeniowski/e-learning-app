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
            " or upper(course.name) like upper(concat('%', :keyword,'%'))",
            countQuery = "select count(course) from Course course where course.creatorId = " +
                    "(select user.id from User user " +
                    " where upper(user.firstName) like upper(concat('%', :keyword,'%'))" +
                    " or upper(user.lastName) like upper(concat('%', :keyword, '%')))" +
                    " or upper(course.name) like upper(concat('%', :keyword,'%'))")
    Page<Course> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("select course.name from Course course where course.id = :courseId")
    String findCourseNameById(@Param("courseId") Long courseId);

    @Query("select course.creatorId from Course course where course.id = :courseId")
    Long findCreatorIdByCourseId(@Param("courseId") Long courseId);

    void deleteAllByCreatorId(Long id);

    Page<Course> findAllByCreatorId(Long teacherId, Pageable pageable);

    @Query(value = "select course from Course course where" +
            " course.creatorId = :teacherId" +
            " and upper(course.name) like upper(concat('%', :keyword, '%'))",
            countQuery = "select count(course) from Course course where" +
                    " course.creatorId = :teacherId" +
                    " and upper(course.name) like upper(concat('%', :keyword, '%'))")
    Page<Course> findAllByCreatorIdAndKeyword(@Param("teacherId") Long teacherId,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);

    @Query(value = "select course from Course course" +
            " join course.users user" +
            " where user.id = :userId",
            countQuery = "select count(course) from Course course" +
                    " join course.users user" +
                    " where user.id = :userId")
    Page<Course> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select course from Course course" +
            " join course.users user" +
            " where user.id = :userId" +
            " and upper(course.name) like upper(concat('%',:keyword, '%'))",
            countQuery = "select count(course) from Course course" +
                    " join course.users user" +
                    " where user.id = :userId" +
                    " and upper(course.name) like upper(concat('%',:keyword, '%'))")
    Page<Course> findAllByUserIdAndKeyword(@Param("userId") Long userId,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);

    Boolean existsByCreatorId(Long creatorId);
}
