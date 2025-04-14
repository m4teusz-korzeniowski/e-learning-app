package korzeniowski.mateusz.app.model.course.test.dto;

import jakarta.validation.constraints.NotBlank;
import korzeniowski.mateusz.app.model.course.test.Test;

public class TestNameIdDto {
    private Long id;
    @NotBlank(message = "*pole nie może być puste")
    private String name;

    public TestNameIdDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public static TestNameIdDto map(Test test){
        return new TestNameIdDto(test.getId(), test.getName());
    }
}
