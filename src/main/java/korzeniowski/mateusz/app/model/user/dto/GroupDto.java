package korzeniowski.mateusz.app.model.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import korzeniowski.mateusz.app.model.user.Group;

public class GroupDto {
    @NotBlank(message = "*pole nie może być puste!")
    @NotNull(message = "*pole nie może być puste!")
    @Size(max = 60, message = "*nazwa może składać się maksymalnie z 60 znaków!")
    private String name;
    private Long id;

    public GroupDto(String name, Long id) {
        this.name = name;
        this.id = id;
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

    public static GroupDto map(Group group) {
        return new GroupDto(group.getName(), group.getId());
    }
}
