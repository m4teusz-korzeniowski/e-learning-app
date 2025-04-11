package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.course.test.Test;
import korzeniowski.mateusz.app.model.course.test.dto.TestDisplayDto;
import korzeniowski.mateusz.app.model.course.test.dto.TestNameIdDto;
import korzeniowski.mateusz.app.repository.ModuleRepository;
import korzeniowski.mateusz.app.repository.TestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TestService {
    private final TestRepository testRepository;
    private final ModuleService moduleService;

    public TestService(TestRepository testRepository, ModuleService moduleService) {
        this.testRepository = testRepository;
        this.moduleService = moduleService;
    }

    public Optional<TestDisplayDto> findTestById(Long id) {
        return testRepository.findTestById(id).map(TestDisplayDto::map);
    }

    public void updateTest(Long testId, TestNameIdDto testDto) {
        Optional<Test> test = testRepository.findTestById(testId);
        if (test.isPresent()) {
            test.get().setName(testDto.getName());
            testRepository.save(test.get());
        }
    }

    @Transactional
    public void createTest(Long moduleId) {
        Test test = new Test();
        test.setName("Nazwa testu");
        moduleService.addTestToModule(moduleId, test);
        testRepository.save(test);
    }

    public boolean testExists(Long testId) {
        return testRepository.existsById(testId);
    }

    @Transactional
    public void deleteTest(Long testId) {
        testRepository.findTestById(testId).ifPresent(testRepository::delete);
    }
}
