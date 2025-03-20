package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.user.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    Optional<UserRole> findByName(String role);
}
