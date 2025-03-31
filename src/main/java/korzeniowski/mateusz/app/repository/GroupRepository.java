package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.user.Group;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GroupRepository extends CrudRepository<Group, Long> {
    List<Group> findAllBy();

    Group findByName(String name);
}
