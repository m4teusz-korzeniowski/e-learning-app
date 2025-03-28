package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByLastNameContainsOrFirstNameContainsOrEmailContainsOrderByFirstName(
            String lastName, String firstName, String email);
    List<User> findAllByOrderByFirstName();
}
