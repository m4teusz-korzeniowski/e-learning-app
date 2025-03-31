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

}
