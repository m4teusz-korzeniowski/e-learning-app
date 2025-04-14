package korzeniowski.mateusz.app.model.course.module.dto;

import jakarta.validation.constraints.NotBlank;
import korzeniowski.mateusz.app.model.course.module.ModuleItem;

public class ModuleItemDisplayDto {
    private Long id;
    @NotBlank(message = "*pole nie może być puste")
    private String name;
    private String description;
    private String filePath;

    public ModuleItemDisplayDto(Long id, String name, String description, String filePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.filePath = filePath;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static ModuleItemDisplayDto map(ModuleItem item) {
        return new ModuleItemDisplayDto(item.getId(), item.getName(), item.getDescription(), item.getFileUrl());
    }
}
