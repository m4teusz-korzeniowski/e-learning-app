package korzeniowski.mateusz.app.model.course.module;

import jakarta.persistence.*;
import korzeniowski.mateusz.app.model.course.test.Test;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany
    @JoinColumn(name = "module_id")
    private List<ModuleItem> items = new ArrayList<ModuleItem>();
    @OneToMany
    @JoinColumn(name  = "module_id")
    private List<Test> test;

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
}
