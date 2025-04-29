package korzeniowski.mateusz.app.model.course.test.dto;

import korzeniowski.mateusz.app.model.course.test.Question;

import java.util.List;

public class QuestionAttemptDto {
    private Long id;
    private String description;
    private Double score;
    private String category;
    private String questionType;
    private List<AnswerAttemptDto> answers;

    public QuestionAttemptDto() {
    }

    public QuestionAttemptDto(Long id, String description, Double score, String category,
                              String questionType, List<AnswerAttemptDto> answers) {
        this.id = id;
        this.description = description;
        this.score = score;
        this.category = category;
        this.questionType = questionType;
        this.answers = answers;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public List<AnswerAttemptDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerAttemptDto> answers) {
        this.answers = answers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static QuestionAttemptDto map(Question question) {
        return new QuestionAttemptDto(
                question.getId(),
                question.getDescription(), question.getScore(),
                question.getCategory(),
                question.getQuestionType().name(),
                question.getAnswers().stream().map(AnswerAttemptDto::map).toList());
    }

    public Boolean didUserAnswer() {
        for (AnswerAttemptDto answer : answers) {
            if (answer.getUserAnswer().equals(true)) {
                return true;
            }
        }
        return false;
    }
}
