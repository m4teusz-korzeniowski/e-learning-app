package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.module.Module;
import org.springframework.data.repository.CrudRepository;

public interface ModuleRepository extends CrudRepository<Module, Long> {

}
