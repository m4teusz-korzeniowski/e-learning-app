package korzeniowski.mateusz.app.model.course.test;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class AttemptId implements Serializable {
    private Long userId;
    private Long testId;

    public AttemptId() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }
}
