package korzeniowski.mateusz.app.model.course.test.dto;

import korzeniowski.mateusz.app.model.course.test.Attempt;

import java.util.List;
import java.util.stream.Collectors;

public class UserResultsDto {

    private String testName;
    private List<UserAttemptDto> attempts;

    public UserResultsDto(String testName, List<UserAttemptDto> attempts) {
        this.testName = testName;
        this.attempts = attempts;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }


    public List<UserAttemptDto> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<UserAttemptDto> attempts) {
        this.attempts = attempts;
    }

    public static List<UserResultsDto> map(List<Attempt> attempts) {
        return attempts.stream()
                .collect(Collectors.groupingBy(a -> a.getTest().getName()))
                .entrySet().stream()
                .map(entry -> new UserResultsDto(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(UserAttemptDto::map)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}
