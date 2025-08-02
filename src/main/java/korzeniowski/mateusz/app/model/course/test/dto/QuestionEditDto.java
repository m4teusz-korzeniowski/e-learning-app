package korzeniowski.mateusz.app.model.course.test.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import korzeniowski.mateusz.app.model.course.test.Answer;
import korzeniowski.mateusz.app.model.course.test.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionEditDto {
    private Long id;
    @NotBlank(message = "*pole nie może być puste!")
    @Size(max = 5000, message = "*przekroczono maksymalny rozmiar opisu!")
    private String description;
    @NotNull(message = "*pole nie może być puste!")
    @Min(value = 1, message = "*ilość punktów musi wynosić co najmniej 1!")
    private Double score;
    @NotBlank(message = "*pole nie może być puste!")
    private String type;
    @Size(max = 50, message = "*przekroczono maksymalny rozmiar dla kategorii!")
    private String category;
    @Valid
    private List<AnswerEditDto> answers;
    private Long courseId;
    private Long testId;
    private String fileUrl;

    public QuestionEditDto(Long id, String description, Double score, String type,
                           String category, List<AnswerEditDto> answers,
                           Long courseId, Long testId, String fileUrl) {
        this.id = id;
        this.description = description;
        this.score = score;
        this.type = type;
        this.category = category;
        this.answers = answers;
        this.courseId = courseId;
        this.testId = testId;
        this.fileUrl = fileUrl;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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
                question.getCategory(),
                answers,
                question.getTest().getModule().getCourse().getId(),
                question.getTest().getId(),
                question.getFileUrl()
        );
    }
}
