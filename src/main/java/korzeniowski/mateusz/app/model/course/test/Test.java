package korzeniowski.mateusz.app.model.course.test;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Integer numberOfQuestions;
    @OneToMany
    @JoinColumn(name = "test_id")
    private List<Question> questions = new ArrayList<Question>();
    @OneToMany
    @JoinColumn(name = "test_id")
    private List<Result> results = new ArrayList<>();

}
