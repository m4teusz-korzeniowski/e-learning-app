package korzeniowski.mateusz.app.model.course.module.dto;

import korzeniowski.mateusz.app.model.course.module.ModuleItem;
import org.springframework.context.annotation.Bean;

public class ModuleItemEditDto {
    private Long id;
    private String description;
    private String fileUrl;

    public ModuleItemEditDto(Long id, String description, String fileUrl) {
        this.id = id;
        this.description = description;
        this.fileUrl = fileUrl;
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

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public static ModuleItemEditDto map(ModuleItem item) {
        return new ModuleItemEditDto(item.getId(), item.getDescription(), item.getFileUrl());
    }
}
