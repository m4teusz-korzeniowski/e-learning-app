package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.exceptions.AttemptInProgressException;
import korzeniowski.mateusz.app.exceptions.AttemptOverviewDisabledException;
import korzeniowski.mateusz.app.exceptions.EmptyQuestionBankException;
import korzeniowski.mateusz.app.exceptions.ExceededTestAttemptsException;
import korzeniowski.mateusz.app.model.course.test.*;
import korzeniowski.mateusz.app.model.course.test.dto.*;
import korzeniowski.mateusz.app.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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
        state.setLastModified(LocalDateTime.now());
        attemptStateRepository.save(state);
    }

    private boolean isUserTeacher(Long userId) {
        Optional<String> teacher = userRepository.findUserByRoleAndId(userId, "TEACHER");
        return teacher.isPresent();
    }

    private boolean isUserDemoUser(Long userId) {
        return userRepository.findUserByRoleAndId(userId, "DEMO").isPresent();
    }

    private void deleteAttempt(Long userId, Long testId) {
        attemptRepository.deleteByUserIdAndTestId(userId, testId);
    }

    @Transactional
    public boolean createAttemptIfAvailable(Long userId, TestAttemptDto test) {
        if (test.getStartTime() != null && test.getStartTime().isAfter(LocalDateTime.now())) {
            throw new DateTimeException("*test nie został jeszcze otwarty!");
        }
        if (test.getEndTime() != null && test.getEndTime().isBefore(LocalDateTime.now())) {
            throw new DateTimeException("*test został już zamknięty!");
        }
        if (test.getMaxAttempts() != null) {
            if (isUserTeacher(userId)) {
                deleteAttempt(userId, test.getTestId());
            }
            if (attemptRepository.countByUserIdAndTestId(userId, test.getTestId()) < test.getMaxAttempts()) {
                createNewAttempt(userId, test.getTestId());
                return true;
            } else {
                throw new ExceededTestAttemptsException("*przekroczono limit podejść do testu!");
            }
        } else {
            if (isUserDemoUser(userId)) {
                deleteAttempt(userId, test.getTestId());
            }
            createNewAttempt(userId, test.getTestId());
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

    public void updateAttemptState(Long attemptStateId, TestAttemptDto attempt, Integer questionNo) {
        attemptStateRepository.findById(attemptStateId).ifPresent(attemptState -> {
            attemptState.setAnswersGivenJson(attempt.toJson());
            attemptState.setCurrentQuestionAttempt(questionNo);
            attemptState.setLastModified(LocalDateTime.now());
            attemptStateRepository.save(attemptState);
        });
    }

    public void setAttemptStartTime(TestAttemptDto attempt, LocalDateTime startTime) {
        attempt.setAttemptStartTime(startTime);
    }

    private List<Question> chooseQuestions(List<Question> bank, int numberToSelect) {
        Map<String, List<Question>> categoryMap = new LinkedHashMap<>();
        for (Question question : bank) {
            String category = question.getCategory() != null ? question.getCategory() : "Brak";
            categoryMap.computeIfAbsent(category, k -> new ArrayList<>()).add(question);
        }

        List<List<Question>> groupedByCategory = new ArrayList<>(categoryMap.values());

        List<Question> selected = new ArrayList<>();
        int categoryIndex = 0;
        while (selected.size() < numberToSelect && !groupedByCategory.isEmpty()) {
            List<Question> currentCategory = groupedByCategory.get(categoryIndex % groupedByCategory.size());
            if (!currentCategory.isEmpty()) {
                Collections.shuffle(currentCategory);
                selected.add(currentCategory.remove(0));
            } else {
                groupedByCategory.remove(categoryIndex % groupedByCategory.size());
                continue;
            }
            categoryIndex++;
        }
        return selected;
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
            Collections.shuffle(questions);
            attempt.setQuestions(questions.stream().map(QuestionAttemptDto::map).toList());
        } else {
            List<Question> selected = chooseQuestions(questions, attempt.getNumberOfQuestions());
            Collections.shuffle(selected);
            attempt.setQuestions(selected.stream().map(QuestionAttemptDto::map).toList());
        }
        return attempt;
    }

    public void updateUserAnswers(TestAttemptDto attempt,
                                  int questionNumber,
                                  List<Integer> answers) {
        List<AnswerAttemptDto> questionAnswers = attempt.getQuestions().get(questionNumber - 1).getAnswers();
        questionAnswers.forEach(answer -> answer.setUserAnswer(false));
        answers.forEach(answer -> questionAnswers.get(answer).setUserAnswer(true));
    }


    private double computeMultiplier(QuestionAttemptDto question) {
        int numberOfCorrectAnswers = 0;
        int numberOfUserCorrectAnswers = 0;
        for (AnswerAttemptDto answer : question.getAnswers()) {
            if (answer.getUserAnswer() && answer.getCorrectAnswer()) {
                numberOfUserCorrectAnswers++;
            }
            if (answer.getUserAnswer() && !answer.getCorrectAnswer()) {
                numberOfUserCorrectAnswers--;
            }
            if (answer.getCorrectAnswer()) {
                numberOfCorrectAnswers++;
            }
        }
        if (numberOfUserCorrectAnswers < 0) {
            return 0.0;
        }
        return numberOfUserCorrectAnswers / (double) numberOfCorrectAnswers;
    }

    private boolean isAnswerCorrect(QuestionAttemptDto question) {
        for (AnswerAttemptDto answer : question.getAnswers()) {
            if (answer.getUserAnswer() && answer.getCorrectAnswer()) {
                return true;
            }
        }
        return false;
    }

    private double computeScore(List<QuestionAttemptDto> questions) {
        double score = 0.0;
        double multiplier = 1.0;
        for (QuestionAttemptDto question : questions) {
            if (question.getQuestionType().equals(QuestionType.SINGLE_CHOICE.name())) {
                if (isAnswerCorrect(question)) {
                    multiplier = 1.0;
                } else {
                    multiplier = 0.0;
                }
            } else if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE.name())) {
                multiplier = computeMultiplier(question);
            }
            score += question.getScore() * multiplier;
        }
        return score;
    }

    private double getMaxScore(List<QuestionAttemptDto> questions) {
        double maxScore = 0.0;
        for (QuestionAttemptDto question : questions) {
            maxScore += question.getScore();
        }
        return maxScore;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        return BigDecimal.valueOf(value)
                .setScale(places, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Transactional
    public void leaveOnlyBestAttempt(Long userId, Long testId) {
        List<Attempt> completedAttempts = attemptRepository.findAllByStatus(userId, testId, AttemptStatus.COMPLETED);
        if (completedAttempts.size() >= 2) {
            Attempt best = Collections.max(completedAttempts, Comparator.comparing(Attempt::getMark));
            completedAttempts.stream()
                    .filter(attempt -> !attempt.getId().equals(best.getId()))
                    .forEach(attemptRepository::delete);
        }
    }


    @Transactional
    public void finishAttempt(Long attemptId, TestAttemptDto dto) {
        attemptRepository.findById(attemptId).ifPresent(attempt -> {
            attempt.setEndedAt(LocalDateTime.now());
            attempt.setAnswersGivenJson(dto.toJson());
            double score = computeScore(dto.getQuestions());
            double maxScore = getMaxScore(dto.getQuestions());
            attempt.setScore(round(score, 2));
            attempt.setMark(round(score / maxScore * 100.00, 2));
            if (dto.getMaxAttempts() == null) {
                Long userId = attempt.getUser().getId();
                Long testId = dto.getTestId();
                leaveOnlyBestAttempt(userId, testId);
                Optional<Attempt> previousBest = attemptRepository.findByStatus(userId, testId, AttemptStatus.COMPLETED);
                if (previousBest.isPresent()) {
                    Attempt previous = previousBest.get();
                    if (previous.getMark() < attempt.getMark()) {
                        attemptRepository.delete(previous);
                        attempt.setStatus(AttemptStatus.COMPLETED);
                        attemptRepository.save(attempt);
                    } else {
                        attemptRepository.delete(attempt);
                    }
                } else {
                    attempt.setStatus(AttemptStatus.COMPLETED);
                    attemptRepository.save(attempt);
                }
                attemptStateRepository.findByAttemptId(attemptId).ifPresent(attemptStateRepository::delete);
                return;
            }
            attempt.setStatus(AttemptStatus.COMPLETED);
            attemptRepository.save(attempt);
            attemptStateRepository.findByAttemptId(attemptId).ifPresent(attemptStateRepository::delete);
        });
    }

    public List<AttemptDisplayDto> findAttemptsByUserAndTest(Long userId, Long testId) {
        return attemptRepository.findAllByUserIdAndTestId(userId, testId)
                .stream().map(AttemptDisplayDto::map).toList();
    }

    public Long getRemainingTime(TestAttemptDto attempt) {
        if (attempt.getDuration() == null) {
            return null;
        }
        long elapsed = Duration.between(attempt.getAttemptStartTime(), LocalDateTime.now()).toSeconds();
        return attempt.getDuration() * 60 - elapsed;
    }

    public TestAttemptDto findAttemptOverview(Long attemptId) {
        Optional<Attempt> attempt = attemptRepository.findById(attemptId);
        if (attempt.isPresent()) {
            if (attempt.get().getStatus() == AttemptStatus.IN_PROGRESS) {
                throw new AttemptOverviewDisabledException("*nie można podejżeć aktywnej próby!");
            }
            TestAttemptDto dto = TestAttemptDto.fromJson(attempt.get().getAnswersGivenJson());
            if (dto.getOverviewEnabled()) {
                return dto;
            } else {
                throw new AttemptOverviewDisabledException("*podgląd próby nie jest dozwolony!");
            }
        }
        throw new NoSuchElementException("*próba nie istnieje!");
    }

    public Long findTestIdFromAttempt(Long attemptId) {
        Optional<Attempt> attempt = attemptRepository.findById(attemptId);
        if (attempt.isPresent()) {
            return attempt.get().getTest().getId();
        } else {
            throw new NoSuchElementException("*próba nie istnieje!");
        }
    }

    public List<UserResultsDto> findUserResultsByCourseId(Long courseId, Long userId) {
        List<Attempt> userAttempts = attemptRepository
                .findAllUserAttemptsByCourseId(userId, courseId, AttemptStatus.COMPLETED);
        return UserResultsDto.map(userAttempts);
    }

    public boolean noneAnsweredChecked(QuestionAttemptDto question) {
        for (AnswerAttemptDto answer : question.getAnswers()) {
            if (answer.getUserAnswer()) {
                return false;
            }
        }
        return true;
    }

}

