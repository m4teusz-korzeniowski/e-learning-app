package korzeniowski.mateusz.app.model.course.test.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class QuestionsCreationDto {
    @NotNull(message = "*pole nie może być puste!")
    @Min(value = 1, message = "*ilość pytań musi być równa co najmniej 1!")
    @Max(value = 100, message = "*maksymalnie można utworzyć na raz 100 pytań!")
    private  Integer numberOfQuestions;

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }
}
