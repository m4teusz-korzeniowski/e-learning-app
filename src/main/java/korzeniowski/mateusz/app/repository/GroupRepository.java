package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.user.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllBy();

    Group findByName(String name);

    Page<Group> findByNameContainsIgnoreCase(String keyword, Pageable pageable);
}
