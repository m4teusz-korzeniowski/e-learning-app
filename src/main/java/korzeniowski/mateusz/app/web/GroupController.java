package korzeniowski.mateusz.app.web;

import jakarta.validation.Valid;
import korzeniowski.mateusz.app.model.user.dto.GroupDto;
import korzeniowski.mateusz.app.service.GroupService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/admin/groups")
    public String showGroupPanel() {
        return "groups";
    }

    @GetMapping("/admin/groups/create")
    public String groupCreation(@ModelAttribute("group") GroupDto group) {
        return "group-creation";
    }

    @PostMapping("/admin/groups/create")
    public String createGroup(@ModelAttribute("group") @Valid GroupDto group, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return groupCreation(group);
        }
        groupService.createGroup(group);
        return "redirect:/admin/groups";
    }
}
