package korzeniowski.mateusz.app.model.course.test.dto;

import korzeniowski.mateusz.app.model.course.test.Answer;
import korzeniowski.mateusz.app.model.course.test.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionEditDto {
    private Long id;
    private String description;
    private Double score;
    private String type;
    private List<AnswerEditDto> answers;
    private Long courseId;
    private Long testId;

    public QuestionEditDto(Long id, String description, Double score, String type, List<AnswerEditDto> answers, Long courseId, Long testId) {
        this.id = id;
        this.description = description;
        this.score = score;
        this.type = type;
        this.answers = answers;
        this.courseId = courseId;
        this.testId = testId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AnswerEditDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerEditDto> answers) {
        this.answers = answers;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public static QuestionEditDto map(Question question) {
        List<AnswerEditDto> answers = new ArrayList<>();
        for (Answer answer : question.getAnswers()) {
            answers.add(new AnswerEditDto(answer.getId(), answer.getContent(), answer.getCorrect()));
        }
        return new QuestionEditDto(question.getId(),
                question.getDescription(),
                question.getScore(),
                question.getQuestionType().name(),
                answers,
                question.getTest().getModule().getCourse().getId(),
                question.getTest().getId());
    }
}
