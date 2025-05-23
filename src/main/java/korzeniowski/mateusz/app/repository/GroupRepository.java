package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.user.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllBy();

    Optional<Group> findByName(String name);

    Page<Group> findByNameContainsIgnoreCase(String keyword, Pageable pageable);
}
