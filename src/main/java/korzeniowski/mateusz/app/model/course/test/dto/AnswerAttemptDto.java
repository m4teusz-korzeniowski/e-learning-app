package korzeniowski.mateusz.app.model.course.test.dto;

import korzeniowski.mateusz.app.model.course.test.Answer;

public class AnswerAttemptDto {
    private String content;
    private Boolean userAnswer;

    public AnswerAttemptDto() {
    }

    public AnswerAttemptDto(String content, Boolean userAnswer) {
        this.content = content;
        this.userAnswer = userAnswer;
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

    public static AnswerAttemptDto map(Answer answer) {
        return new AnswerAttemptDto(answer.getContent(), false);
    }
}
