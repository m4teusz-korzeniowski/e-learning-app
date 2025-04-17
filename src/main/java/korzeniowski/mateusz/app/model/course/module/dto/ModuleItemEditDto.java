package korzeniowski.mateusz.app.model.course.module.dto;

import korzeniowski.mateusz.app.model.course.module.ModuleItem;

public class ModuleItemEditDto {
    private Long id;
    private String description;
    private String fileUrl;
    private String name;
    private Long courseId;

    public ModuleItemEditDto(Long id, String description, String fileUrl, String name, Long courseId) {
        this.id = id;
        this.description = description;
        this.fileUrl = fileUrl;
        this.name = name;
        this.courseId = courseId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public static ModuleItemEditDto map(ModuleItem item) {
        return new ModuleItemEditDto(item.getId(), item.getDescription(),
                item.getFileUrl(), item.getName(), item.getModule().getCourse().getId());
    }
}
