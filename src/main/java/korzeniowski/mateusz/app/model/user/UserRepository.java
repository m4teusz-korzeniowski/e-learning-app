package korzeniowski.mateusz.app.model.user;

import korzeniowski.mateusz.app.model.course.Course;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
