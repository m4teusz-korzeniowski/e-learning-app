package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.user.Group;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, Long> {
}
