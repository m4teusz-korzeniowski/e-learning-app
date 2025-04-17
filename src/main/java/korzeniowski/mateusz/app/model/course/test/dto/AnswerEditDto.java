package korzeniowski.mateusz.app.model.course.test.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import korzeniowski.mateusz.app.model.course.test.Answer;

public class AnswerEditDto {
    private Long id;
    @NotBlank(message = "*pole nie może być puste!")
    @Size(max = 500, message = "*przekroczono maksymalny rozmiar pytania!")
    private String content;
    private Boolean correct;

    public AnswerEditDto(Long id, String content, Boolean correct) {
        this.id = id;
        this.content = content;
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getCorrect() {
        return correct;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public static AnswerEditDto map(Answer answer) {
        return new AnswerEditDto(answer.getId(), answer.getContent(), answer.getCorrect());
    }
}
