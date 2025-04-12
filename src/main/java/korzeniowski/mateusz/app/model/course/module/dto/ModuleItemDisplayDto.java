package korzeniowski.mateusz.app.model.course.module.dto;

import korzeniowski.mateusz.app.model.course.module.ModuleItem;

public class ModuleItemDisplayDto {
    private Long id;
    private String name;
    private String description;

    public ModuleItemDisplayDto(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static ModuleItemDisplayDto map(ModuleItem item) {
        return new ModuleItemDisplayDto(item.getId(), item.getName(), item.getDescription());
    }
}
