package korzeniowski.mateusz.app.model.course.test.dto;

import korzeniowski.mateusz.app.model.course.test.Answer;

public class AnswerAttemptDto {
    private String content;
    private Boolean userAnswer;
    private Boolean correctAnswer;

    public AnswerAttemptDto() {
    }

    public AnswerAttemptDto(String content, Boolean userAnswer, Boolean correctAnswer) {
        this.content = content;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(Boolean userAnswer) {
        this.userAnswer = userAnswer;
    }

    public Boolean getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public static AnswerAttemptDto map(Answer answer) {
        return new AnswerAttemptDto(answer.getContent(), false, answer.getCorrect());
    }
}
