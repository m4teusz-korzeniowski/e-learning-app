package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.module.ModuleItem;
import org.springframework.data.repository.CrudRepository;

public interface ModuleItemRepository extends CrudRepository<ModuleItem, Long> {
}
