package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.exceptions.AttemptInProgressException;
import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.model.course.test.AttemptStatus;
import korzeniowski.mateusz.app.model.course.test.Question;
import korzeniowski.mateusz.app.model.course.test.Test;
import korzeniowski.mateusz.app.model.course.test.dto.TestDisplayDto;
import korzeniowski.mateusz.app.model.course.test.dto.TestEditDto;
import korzeniowski.mateusz.app.model.course.test.dto.TestNameIdDto;
import korzeniowski.mateusz.app.repository.AttemptRepository;
import korzeniowski.mateusz.app.repository.TestRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class TestService {
    private final TestRepository testRepository;
    private final ModuleService moduleService;
    private final static int MAX_LENGTH_OF_TEST_NAME = 60;
    private final AttemptRepository attemptRepository;

    public TestService(TestRepository testRepository, ModuleService moduleService, AttemptRepository attemptRepository) {
        this.testRepository = testRepository;
        this.moduleService = moduleService;
        this.attemptRepository = attemptRepository;
    }

    public Optional<TestDisplayDto> findTestById(Long id) {
        return testRepository.findTestById(id).map(TestDisplayDto::map);
    }

    public void updateTest(Long testId, TestNameIdDto testDto) {
        Optional<Test> test = testRepository.findTestById(testId);
        if (test.isPresent()) {
            if (testDto.getName().length() > MAX_LENGTH_OF_TEST_NAME) {
                throw new DataIntegrityViolationException("*przekroczono rozmiar dla nazwy testu!");
            }
            test.get().setName(testDto.getName());
            testRepository.save(test.get());
        }
    }

    @Transactional
    public void createTest(Long moduleId) {
        Test test = new Test();
        test.setName("Nazwa testu");
        test.setNumberOfQuestions(1);
        test.setOverviewEnabled(false);
        moduleService.addTestToModule(moduleId, test);
        testRepository.save(test);
    }

    public boolean testExists(Long testId) {
        System.out.println("test");
        System.out.println(testRepository.existsById(testId));
        return testRepository.existsById(testId);
    }

    @Transactional
    public void deleteTest(Long testId) {
        if (attemptRepository.hasTestActiveAttempt(testId, AttemptStatus.IN_PROGRESS)) {
            throw new AttemptInProgressException("*nie można usunąć testu posiadającego aktywne próby!");
        }
        testRepository.findTestById(testId).ifPresent(testRepository::delete);
    }

    public Optional<TestEditDto> findTestEditById(Long id) {
        return testRepository.findById(id).map(TestEditDto::map);
    }

    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return;
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("*data otwarcia nie może późniejsza niż data zamknięcia!");
        }
        Duration duration = Duration.between(start, end);
        if (duration.toMinutes() < 30) {
            throw new IllegalArgumentException(
                    "*róznica między otwarciem a zamknięciem musi wynosić co najmniej 30 minut!");
        }
    }

    public void updateTestSettings(Long testId, TestEditDto dto) {
        validateDates(dto.getStart(), dto.getEnd());
        Optional<Test> testEdit = testRepository.findTestById(testId);
        testEdit.ifPresent(test -> {
            test.setDescription(dto.getDescription());
            test.setNumberOfQuestions(dto.getNumberOfQuestions());
            test.setMaxAttempts(dto.getMaxAttempts());
            test.setDuration(dto.getDuration());
            test.setStartTime(dto.getStart());
            test.setEndTime(dto.getEnd());
            test.setOverviewEnabled(dto.getOverviewEnabled());
            testRepository.save(test);
        });
    }

    @Transactional
    public void addQuestionToTest(Long testId, Question question) {
        Optional<Test> test = testRepository.findTestById(testId);
        if (test.isPresent()) {
            question.setTest(test.get());
            test.get().getQuestions().add(question);
        } else {
            throw new NoSuchElementException("Nie znaleziono testu!");
        }
    }

    public Long findCourseIdFromTest(Long testId) {
        Optional<Test> test = testRepository.findTestById(testId);
        if (test.isPresent()) {
            return test.get().getModule().getCourse().getId();
        } else {
            throw new NoSuchElementException("Nie znaleziono testu!");
        }
    }
}
