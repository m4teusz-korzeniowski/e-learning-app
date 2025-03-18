package korzeniowski.mateusz.app.model.course.test.dto;

import korzeniowski.mateusz.app.model.course.test.Result;

public class ResultDto {
    private Double score;

    public ResultDto(Double score) {
        this.score = score;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public static ResultDto map(Result result) {
        return new ResultDto(result.getScore());
    }
}
