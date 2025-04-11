package korzeniowski.mateusz.app.model.course.test;

import jakarta.persistence.*;
import korzeniowski.mateusz.app.model.user.User;

@Entity
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double score;

    @ManyToOne
    @JoinColumn(name="test_id")
    private Test test;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
