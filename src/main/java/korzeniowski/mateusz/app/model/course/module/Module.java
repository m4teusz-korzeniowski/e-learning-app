package korzeniowski.mateusz.app.model.course.module;

import jakarta.persistence.*;
import korzeniowski.mateusz.app.model.course.Course;
import korzeniowski.mateusz.app.model.course.test.Test;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "module")
    private List<ModuleItem> items = new ArrayList<>();
    @OneToMany(mappedBy = "module")
    private List<Test> test= new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

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

    public List<ModuleItem> getItems() {
        return items;
    }

    public void setItems(List<ModuleItem> items) {
        this.items = items;
    }

    public List<Test> getTest() {
        return test;
    }

    public void setTest(List<Test> test) {
        this.test = test;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
