package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.model.user.dto.GroupDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.GroupService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class GroupController {

    private final UserService userService;
    private final GroupService groupService;

    public GroupController(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

    @GetMapping("/admin/groups")
    public String showGroupPanel(Principal principal, HttpSession session) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        return "groups";
    }

    @GetMapping("/admin/groups/create")
    public String groupCreation(@ModelAttribute("group") GroupDto group, Principal principal,
                                HttpSession session) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        return "group-creation";
    }

    @PostMapping("/admin/groups/create")
    public String createGroup(@ModelAttribute("group") @Valid GroupDto group, BindingResult bindingResult,
                              Principal principal, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return groupCreation(group, principal, session);
        }
        groupService.createGroup(group);
        return "redirect:/admin/groups";
    }
}
