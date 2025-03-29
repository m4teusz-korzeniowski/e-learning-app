package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    /*@Query("select user from User user where user.firstName like %:keyword%" +
            " or user.lastName like %:keyword%" +
            " or user.email like %:keyword%")
    Page<User> findByLastNameContainsOrFirstNameContainsOrEmailContains(
            @Param("keyword") String keyword, Pageable pageable);*/

    Page<User> findByLastNameContainsOrFirstNameContainsOrEmailContains(
            String lastName, String firstName, String email, Pageable pageable);
    Page<User> findAllBy(Pageable pageable);

    Optional<User> findByPesel(String pesel);
}
