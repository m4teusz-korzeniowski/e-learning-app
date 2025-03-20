package korzeniowski.mateusz.app.model.course.module.dto;

import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.model.course.test.dto.TestNameIdDto;

import java.util.List;

public class ModuleDisplayDto {
    private String name;
    private List<ModuleItemDisplayDto> items;
    private List<TestNameIdDto> tests;

    public ModuleDisplayDto(String name, List<ModuleItemDisplayDto> items, List<TestNameIdDto> tests) {
        this.name = name;
        this.items = items;
        this.tests = tests;
    }

    public ModuleDisplayDto() {
    }

    public String getName() {
        return name;
    }

    public List<ModuleItemDisplayDto> getItems() {
        return items;
    }

    public List<TestNameIdDto> getTests() {
        return tests;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItems(List<ModuleItemDisplayDto> items) {
        this.items = items;
    }

    public void setTests(List<TestNameIdDto> tests) {
        this.tests = tests;
    }

    public static ModuleDisplayDto map(Module module) {
        return new ModuleDisplayDto(
                module.getName(),
                module.getItems().stream().map(ModuleItemDisplayDto::map).toList(),
                module.getTest().stream().map(TestNameIdDto::map).toList()
                );
    }
}
