package korzeniowski.mateusz.app.model.course.test.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import korzeniowski.mateusz.app.model.course.test.Test;

import java.util.ArrayList;
import java.util.List;

public class TestAttemptDto {
    private Long testId;
    private String name;
    private Integer numberOfQuestions;
    private List<QuestionAttemptDto> questions;
    private Integer maxAttempts;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public TestAttemptDto() {
    }

    public TestAttemptDto(Long testId, String name, Integer numberOfQuestions,
                          List<QuestionAttemptDto> questions, Integer maxAttempts) {
        this.testId = testId;
        this.name = name;
        this.numberOfQuestions = numberOfQuestions;
        this.questions = questions;
        this.maxAttempts = maxAttempts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public List<QuestionAttemptDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionAttemptDto> questions) {
        this.questions = questions;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public static TestAttemptDto map(Test test) {
        return new TestAttemptDto(test.getId(), test.getName(), test.getNumberOfQuestions(),
                new ArrayList<>(), test.getMaxAttempts());
    }

    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static TestAttemptDto fromJson(String json) {
        try {
            return objectMapper.readValue(json, TestAttemptDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
