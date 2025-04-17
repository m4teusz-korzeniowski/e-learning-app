package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.course.test.Answer;
import korzeniowski.mateusz.app.repository.AnswerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final QuestionService questionService;

    public AnswerService(AnswerRepository answerRepository, QuestionService questionService) {
        this.answerRepository = answerRepository;
        this.questionService = questionService;
    }

    @Transactional
    public void createAnswer(Long questionId) {
        Answer answer = new Answer();
        answer.setContent("Przykładowa odpowiedź na pytanie");
        answer.setCorrect(false);
        questionService.addAnswerToQuestion(answer, questionId);
        answerRepository.save(answer);
    }

    public boolean answerExist(Long answerId) {
        return answerRepository.existsById(answerId);
    }

    public long findQuestionId(Long answerId) {
        Optional<Answer> answer = answerRepository.findById(answerId);
        if (answer.isPresent()) {
            return answer.get().getQuestion().getId();
        } else {
            throw new NoSuchElementException("Answer with id " + answerId + " not found");
        }
    }

    @Transactional
    public void deleteAnswer(Long answerId) {
        answerRepository.findById(answerId).ifPresent(answerRepository::delete);
    }

}
