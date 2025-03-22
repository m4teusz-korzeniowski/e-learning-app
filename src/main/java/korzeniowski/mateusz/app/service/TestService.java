package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.course.test.dto.TestDisplayDto;
import korzeniowski.mateusz.app.repository.TestRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TestService {
    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public Optional<TestDisplayDto> findTestById(Long id) {
        return testRepository.findTestById(id).map(TestDisplayDto::map);
    }
}
