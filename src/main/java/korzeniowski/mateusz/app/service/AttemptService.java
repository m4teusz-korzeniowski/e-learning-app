package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.exceptions.EmptyQuestionBankException;
import korzeniowski.mateusz.app.model.course.test.*;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionAttemptDto;
import korzeniowski.mateusz.app.model.course.test.dto.TestAttemptDto;
import korzeniowski.mateusz.app.repository.AttemptRepository;
import korzeniowski.mateusz.app.repository.AttemptStateRepository;
import korzeniowski.mateusz.app.repository.TestRepository;
import korzeniowski.mateusz.app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttemptService {
    private final AttemptRepository attemptRepository;
    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final AttemptStateRepository attemptStateRepository;

    public AttemptService(AttemptRepository attemptRepository, TestRepository testRepository, UserRepository userRepository, AttemptStateRepository attemptStateRepository) {
        this.attemptRepository = attemptRepository;
        this.testRepository = testRepository;
        this.userRepository = userRepository;
        this.attemptStateRepository = attemptStateRepository;
    }

    public Optional<TestAttemptDto> findTestAttempt(Long testId) {
        return testRepository.findById(testId).map(TestAttemptDto::map);
    }

    public boolean isAttemptInProgress(Long userId, Long testId) {
        Optional<Attempt> attempt = attemptRepository.findByStatus(userId, testId, AttemptStatus.IN_PROGRESS);
        return attempt.isPresent();
    }


    private void bindAttemptToUserAndTest(Attempt attempt, Long userId, Long testId) {
        testRepository.findById(testId).ifPresent(test -> {
            attempt.setTest(test);
            test.getAttempts().add(attempt);

        });
        userRepository.findById(userId).ifPresent(user -> {
            attempt.setUser(user);
            user.getAttempts().add(attempt);
        });
    }

    private void createNewAttempt(Long userId, Long testId) {
        Attempt attempt = new Attempt();
        attempt.setStatus(AttemptStatus.IN_PROGRESS);
        attempt.setStartedAt(LocalDateTime.now());
        bindAttemptToUserAndTest(attempt, userId, testId);
        attemptRepository.save(attempt);
        attemptRepository.flush();
        createNewAttemptState(attempt);
    }

    private void createNewAttemptState(Attempt attempt) {
        AttemptState state = new AttemptState();
        state.setAttempt(attempt);
        state.setStatus(AttemptStatus.IN_PROGRESS);
        state.setCurrentQuestionAttempt(1);
        attemptStateRepository.save(state);
    }

    @Transactional
    public boolean createAttemptIfAvailable(Long userId, TestAttemptDto test) {
        if (test.getMaxAttempts() != null) {
            if (attemptRepository.countByUserIdAndTestId(userId, test.getId()) < test.getMaxAttempts()) {
                createNewAttempt(userId, test.getId());
                return true;
            } else {
                return false;
            }
        } else {
            createNewAttempt(userId, test.getId());
            return true;
        }
    }

    public Long findAttemptId(Long userId, Long testId) {
        Optional<Long> attemptId = attemptRepository.findByUserIdAndTestId(userId, testId);
        if (attemptId.isPresent()) {
            return attemptId.get();
        } else {
            throw new NoSuchElementException("Attempt not found");
        }
    }

    public AttemptState findAttemptState(Long attemptId) {
        Optional<AttemptState> attemptState = attemptStateRepository.findByAttemptId(attemptId);
        if (attemptState.isPresent()) {
            return attemptState.get();
        } else {
            throw new NoSuchElementException("Attempt state not found");
        }
    }

    public TestAttemptDto loadAttempt(AttemptState attemptState) {
        return TestAttemptDto.fromJson(attemptState.getAnswersGivenJson());
    }

    public void updateAttemptState(Long attemptStateId, TestAttemptDto attempt) {
        attemptStateRepository.findById(attemptStateId).ifPresent(attemptState -> {
            attemptState.setAnswersGivenJson(attempt.toJson());
            attemptStateRepository.save(attemptState);
        });
    }


    public TestAttemptDto initializeTest(TestAttemptDto attempt, Long testId,
                                         Long attemptStateId, Long attemptId) {
        List<Question> questions = testRepository.findQuestionsByTestId(testId);
        if (questions.isEmpty()) {
            attemptStateRepository.findById(attemptStateId).ifPresent(attemptStateRepository::delete);
            attemptRepository.findById(attemptId).ifPresent(attemptRepository::delete);
            throw new EmptyQuestionBankException("*nie można uruchomić testu, który nie ma pytań!");
        } else if (questions.size() <= attempt.getNumberOfQuestions()) {
            attempt.setNumberOfQuestions(questions.size());
            attempt.setQuestions(questions.stream().map(QuestionAttemptDto::map).toList());
        } else {
            Map<String, List<Question>> groupedByCategory = new HashMap<>();
            for (Question question : questions) {
                if (question.getCategory() == null) {
                    question.setCategory("Brak");
                }
                groupedByCategory.computeIfAbsent(question.getCategory(), k -> new ArrayList<>()).add(question);
            }
            int numberOfCategories = groupedByCategory.keySet().size();
            int questionPerCategory = attempt.getNumberOfQuestions() / numberOfCategories;
            List<Question> selected = new ArrayList<>();
            for (List<Question> questionsInCategory : groupedByCategory.values()) {
                if (questionsInCategory.size() >= questionPerCategory) {
                    Collections.shuffle(questionsInCategory);
                    List<Question> picked = questionsInCategory.subList(0, questionPerCategory);
                    selected.addAll(picked);
                    questionsInCategory.removeAll(picked);
                } else {
                    selected.addAll(questionsInCategory);
                }
            }
            if (selected.size() < attempt.getNumberOfQuestions()) {
                int remainingQuestionsCount = attempt.getNumberOfQuestions() - selected.size();

                List<Question> remainingQuestions = groupedByCategory.values().stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());

                Collections.shuffle(remainingQuestions);

                List<Question> additional = remainingQuestions.stream()
                        .limit(remainingQuestionsCount)
                        .toList();

                selected.addAll(additional);
            }
            attempt.setQuestions(selected.stream().map(QuestionAttemptDto::map).toList());
        }
        return attempt;
    }
}

