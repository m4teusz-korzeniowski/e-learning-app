package korzeniowski.mateusz.app.model.course.test.dto;

import korzeniowski.mateusz.app.model.course.test.Question;

public class QuestionDisplayDto {
    private Long id;
    private String description;
    private String type;

    public QuestionDisplayDto(Long id, String description, String type) {
        this.id = id;
        this.description = description;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static QuestionDisplayDto map(Question question) {
        return new QuestionDisplayDto(question.getId(),
                question.getDescription(),
                question.getQuestionType().name());
    }
}
