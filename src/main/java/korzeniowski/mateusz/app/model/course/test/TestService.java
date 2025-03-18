package korzeniowski.mateusz.app.model.course.test;

import org.springframework.stereotype.Service;

@Service
public class TestService {
    private TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public Test findTestById(Long id) {
        return testRepository.findTestById(id);
    }


}
