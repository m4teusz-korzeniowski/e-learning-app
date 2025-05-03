package korzeniowski.mateusz.app.model.course.test.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import korzeniowski.mateusz.app.model.course.test.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestAttemptDto {
    private Long testId;
    private String name;
    private Integer numberOfQuestions;
    private List<QuestionAttemptDto> questions;
    private Integer maxAttempts;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
    private LocalDateTime attemptStartTime;

    private final static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public TestAttemptDto() {
    }

    public TestAttemptDto(Long testId, String name, Integer numberOfQuestions,
                          List<QuestionAttemptDto> questions, Integer maxAttempts,
                          LocalDateTime startTime, LocalDateTime endTime, Integer duration) {
        this.testId = testId;
        this.name = name;
        this.numberOfQuestions = numberOfQuestions;
        this.questions = questions;
        this.maxAttempts = maxAttempts;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDateTime getAttemptStartTime() {
        return attemptStartTime;
    }

    public void setAttemptStartTime(LocalDateTime attemptStartTime) {
        this.attemptStartTime = attemptStartTime;
    }


    public static TestAttemptDto map(Test test) {
        return new TestAttemptDto(test.getId(), test.getName(), test.getNumberOfQuestions(),
                new ArrayList<>(), test.getMaxAttempts(), test.getStartTime(), test.getEndTime(),
                test.getDuration());
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
