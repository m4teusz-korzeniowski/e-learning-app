package korzeniowski.mateusz.app.model.course.module.dto;

import korzeniowski.mateusz.app.model.course.module.ModuleItem;

public class ModuleItemDisplayDto {
    private String name;

    public ModuleItemDisplayDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ModuleItemDisplayDto map(ModuleItem item) {
        return new ModuleItemDisplayDto(item.getName());
    }
}
