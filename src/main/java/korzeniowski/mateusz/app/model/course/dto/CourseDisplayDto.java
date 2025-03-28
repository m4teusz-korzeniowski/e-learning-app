package korzeniowski.mateusz.app.model.course.dto;

import korzeniowski.mateusz.app.model.course.Course;
import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.model.course.module.ModuleItem;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleDisplayDto;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleItemDisplayDto;
import korzeniowski.mateusz.app.model.course.test.Test;
import korzeniowski.mateusz.app.model.course.test.dto.TestNameIdDto;

import java.util.ArrayList;
import java.util.List;

public class CourseDisplayDto {
    private Long id;
    private String name;
    private String description;
    private List<ModuleDisplayDto> modules = new ArrayList<>();

    public CourseDisplayDto(Long id, String name, String description, List<ModuleDisplayDto> modules) {
        this.id = id;
        this.name = name;
        this.modules = modules;
        this.description = description;
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

    public List<ModuleDisplayDto> getModules() {
        return modules;
    }

    public void setModules(List<ModuleDisplayDto> modules) {
        this.modules = modules;
    }

    public static CourseDisplayDto map(Course course) {
        List<ModuleDisplayDto> modules = new ArrayList<>();
        for (Module module : course.getModules()) {
            List<ModuleItemDisplayDto> moduleItems = new ArrayList<>();
            List<TestNameIdDto> tests = new ArrayList<>();
            for (ModuleItem item : module.getItems()) {
                moduleItems.add(new ModuleItemDisplayDto(item.getName()));
            }
            for (Test test : module.getTest()) {
                tests.add(new TestNameIdDto(test.getId(), test.getName()));
            }
            modules.add(new ModuleDisplayDto(
                    module.getName(), moduleItems, tests
            ));
        }
        return new CourseDisplayDto(
                course.getId(),
                course.getName(),
                course.getDescription(),
                modules
        );
    }
}
