package korzeniowski.mateusz.app.repository;

import korzeniowski.mateusz.app.model.course.test.Test;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TestRepository extends CrudRepository<Test, Long> {

    public Optional<Test> findTestById(Long id);
}