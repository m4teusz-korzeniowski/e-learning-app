package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select user from User user where upper(user.firstName) like upper(concat('%', :keyword, '%'))" +
            " or upper(user.lastName) like upper(concat('%', :keyword, '%'))" +
            " or upper(user.email) like upper(concat('%', :keyword, '%'))" +
            " or upper(user.group.name) like upper(concat('%', :keyword, '%'))")
    List<User> findAllByKeyword(@Param("keyword") String keyword);


    @Query(value = "select user from User user left join user.userRoles role left join user.group group" +
            " where upper(user.firstName) like upper(concat('%',:keyword, '%'))" +
            " or upper(user.lastName) like upper(concat('%', :keyword, '%'))" +
            " or upper(user.email) like upper(concat('%', :keyword, '%'))" +
            " or upper(coalesce(group.name, 'Brak')) like upper(concat('%', :keyword, '%'))" +
            " or upper(role.name) like upper(concat('%',:keyword,'%'))",
            countQuery = "select count(user)from User user left join user.userRoles role left join user.group group" +
                    " where upper(user.firstName) like upper(concat('%',:keyword, '%'))" +
                    " or upper(user.lastName) like upper(concat('%', :keyword, '%'))" +
                    " or upper(user.email) like upper(concat('%', :keyword, '%'))" +
                    " or upper(coalesce(group.name, 'Brak')) like upper(concat('%', :keyword, '%'))" +
                    " or upper(role.name) like upper(concat('%',:keyword,'%'))")
    Page<User> findAllByKeywordPageable(@Param("keyword") String keyword, Pageable pageable);

    Page<User> findAllBy(Pageable pageable);

    Optional<User> findByPesel(String pesel);

    @Query("select user from User user left join user.userRoles role left join user.group group" +
            " where role.name='STUDENT' and group is null" +
            " and" +
            " (upper(user.firstName) like upper(concat('%', :keyword,'%')) or" +
            " upper(user.lastName) like upper(concat('%', :keyword,'%')) or" +
            " upper(user.email) like upper(concat('%', :keyword,'%')))")
    List<User> findAllStudentsWithoutGroupAndKeyword(@Param("keyword") String keyword);

    @Query("select user from User user left join user.userRoles role" +
            " where role.name='TEACHER'" +
            " and" +
            " (upper(user.firstName) like upper(concat('%', :keyword,'%')) or" +
            " upper(user.lastName) like upper(concat('%', :keyword,'%')) or" +
            " upper(user.email) like upper(concat('%', :keyword,'%')))")
    List<User> findAllTeachersWithKeyword(@Param("keyword") String keyword);

    @Query("select user from User user join Enrollment enroll on user.id = enroll.user_id" +
            " where enroll.course_id = :courseId")
    List<User> findALlByCourseId(@Param("courseId") Long courseId);

    @Query(value = "select user from User user join Enrollment enroll on user.id = enroll.user_id" +
            " where enroll.course_id = :courseId",
            countQuery = "select count(user) from User user join Enrollment enroll on user.id = enroll.user_id" +
                    " where enroll.course_id = :courseId")
    Page<User> findAllByCourseIdPageable(Pageable pageable, @Param("courseId") Long courseId);

    @Query(value = "select user from User user join Enrollment enroll on user.id = enroll.user_id" +
            " where enroll.course_id = :courseId and" +
            " (upper(user.firstName) like upper(concat('%', :keyword,'%')) or" +
            " upper(user.lastName) like upper(concat('%', :keyword,'%')))",
            countQuery = "select count(user) from User user join Enrollment enroll on user.id = enroll.user_id" +
                    " where enroll.course_id = :courseId and" +
                    " (upper(user.firstName) like upper(concat('%', :keyword,'%')) or" +
                    " upper(user.lastName) like upper(concat('%', :keyword,'%')))")
    Page<User> findAllByCourseIdPageableWithKeyword(Pageable pageable, @Param("courseId") Long courseId, @Param("keyword") String keyword);

    @Query("select user from User user where user.group.id = :groupId")
    List<User> findAllByGroupId(@Param("groupId") Long groupId);

    @Query("select role.name from User user" +
            " join user.userRoles role" +
            " where role.name = :roleName and user.id = :userId")
    Optional<String> findUserByRoleAndId(@Param("userId") Long userId, @Param("roleName") String roleName);

    @Query("select concat(user.firstName,' ', user.lastName) from User user" +
            " where user.id = :userId")
    Optional<String> findUserFullNameById(@Param("userId") Long userId);

}
