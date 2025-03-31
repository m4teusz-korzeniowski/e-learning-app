package korzeniowski.mateusz.app.model.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import korzeniowski.mateusz.app.model.user.Group;

public class GroupDto {
    @NotBlank(message = "*pole nie może być puste")
    @NotNull(message = "*pole nie może być puste")
    private String name;

    public GroupDto(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static GroupDto map(Group group) {
        return new GroupDto(group.getName());
    }
}
