package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.course.test.Answer;
import korzeniowski.mateusz.app.model.course.test.Question;
import korzeniowski.mateusz.app.model.course.test.QuestionType;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionDisplayDto;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionEditDto;
import korzeniowski.mateusz.app.repository.AnswerRepository;
import korzeniowski.mateusz.app.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Page<QuestionDisplayDto> users;
        if (!keyword.isBlank()) {
            users = questionRepository.findAllByKeywordAndTestId(
                            keyword, testId, pageable)
                    .map(QuestionDisplayDto::map);
        } else {
            users = questionRepository.findAllByTestId(testId, pageable).map(QuestionDisplayDto::map);
        }
        return users;
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

}
