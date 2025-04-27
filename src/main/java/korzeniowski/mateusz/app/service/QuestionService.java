package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.course.test.Answer;
import korzeniowski.mateusz.app.model.course.test.Question;
import korzeniowski.mateusz.app.model.course.test.QuestionType;
import korzeniowski.mateusz.app.model.course.test.dto.AnswerEditDto;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionDisplayDto;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionEditDto;
import korzeniowski.mateusz.app.repository.AnswerRepository;
import korzeniowski.mateusz.app.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final TestService testService;
    private final AnswerRepository answerRepository;
    private final static int DEFAULT_NUMBER_OF_ANSWERS = 4;

    public QuestionService(QuestionRepository questionRepository, TestService testService, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.testService = testService;
        this.answerRepository = answerRepository;
    }

    @Transactional
    public void createQuestions(int numberOfQuestions, long testId) {
        for (int i = 0; i < numberOfQuestions; i++) {
            Question question = new Question();
            question.setDescription("To jest przykładowe pytanie, które należy zmienić?");
            question.setScore(1.0);
            question.setQuestionType(QuestionType.SINGLE_CHOICE);
            testService.addQuestionToTest(testId, question);
            questionRepository.save(question);
            for (int j = 0; j < DEFAULT_NUMBER_OF_ANSWERS; j++) {
                Answer answer = new Answer();
                answer.setContent("Przykładowa odpowiedź na pytanie");
                if (j == 0) {
                    answer.setCorrect(true);
                } else {
                    answer.setCorrect(false);
                }
                answer.setQuestion(question);
                question.getAnswers().add(answer);
                answerRepository.save(answer);
            }
        }
    }

    public Page<QuestionDisplayDto> findQuestionsPage(int pageNumber, int pageSize, Long testId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return questionRepository.findAllByTestId(testId, pageable).map(QuestionDisplayDto::map);
    }

    public Page<QuestionDisplayDto> findQuestionsPageWithKeyword(int pageNumber, int pageSize,
                                                                 String keyword, Long testId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<QuestionDisplayDto> questions;
        if (!keyword.isBlank()) {
            questions = questionRepository.findAllByKeywordAndTestId(
                            keyword, testId, pageable)
                    .map(QuestionDisplayDto::map);
        } else {
            questions = questionRepository.findAllByTestId(testId, pageable).map(QuestionDisplayDto::map);
        }
        return questions;
    }

    public boolean questionExists(Long questionId) {
        return questionRepository.existsById(questionId);
    }

    @Transactional
    public void removeQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    public Optional<QuestionEditDto> findQuestionById(Long questionId) {
        return questionRepository.findById(questionId).map(QuestionEditDto::map);
    }

    public Long findTestIdFromQuestion(Long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        if (question.isPresent()) {
            return question.get().getTest().getId();
        } else {
            throw new NoSuchElementException("Question not found");
        }
    }

    public boolean maximumNumberOfQuestionReached(int max, long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        if (question.isPresent()) {
            List<Answer> answers = question.get().getAnswers();
            return answers.size() >= max;
        } else {
            throw new NoSuchElementException("Question not found");
        }
    }

    public boolean minimumNumberOfQuestionReached(int min, long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        if (question.isPresent()) {
            List<Answer> answers = question.get().getAnswers();
            return answers.size() < min + 1;
        } else {
            throw new NoSuchElementException("Question not found");
        }
    }

    @Transactional
    public void addAnswerToQuestion(Answer answer, Long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        if (question.isPresent()) {
            answer.setQuestion(question.get());
            question.get().getAnswers().add(answer);
        } else {
            throw new NoSuchElementException("Nie znaleziono pytania!");
        }
    }

    public void editQuestion(QuestionEditDto dto) {
        Optional<Question> question = questionRepository.findById(dto.getId());
        if (question.isPresent()) {
            question.get().setDescription(dto.getDescription());
            question.get().setScore(dto.getScore());
            question.get().setQuestionType(QuestionType.valueOf(dto.getType()));
            question.get().setCategory(dto.getCategory());
        } else {
            throw new NoSuchElementException("Nie znaleziono pytania!");
        }
    }

    public boolean isQuestionTypeOk(QuestionEditDto question) {
        if (QuestionType.valueOf(question.getType()) == QuestionType.SINGLE_CHOICE) {
            int counter = 0;
            for (AnswerEditDto answer : question.getAnswers()) {
                if (answer.getCorrect()) {
                    counter++;
                }
                if (counter >= 2) {
                    return false;
                }
            }
            return counter == 1;
        } else {
            return true;
        }
    }
}
