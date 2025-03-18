package korzeniowski.mateusz.app.model.course.test;

import org.springframework.data.repository.CrudRepository;

public interface TestRepository extends CrudRepository<Test, Long> {

    public Test findTestById(Long id);
}