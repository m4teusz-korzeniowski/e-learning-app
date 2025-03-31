package korzeniowski.mateusz.app.web;

import jakarta.validation.Valid;
import korzeniowski.mateusz.app.exceptions.EmailAlreadyInUseException;
import korzeniowski.mateusz.app.exceptions.NoSuchGroup;
import korzeniowski.mateusz.app.model.user.dto.GroupDto;
import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserGroupEnrollmentDto;
import korzeniowski.mateusz.app.service.GroupService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Controller
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;

    public GroupController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
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

    @GetMapping("/admin/groups/add")
    public String groupEnrollmentForm(@ModelAttribute("enroll") UserGroupEnrollmentDto enroll,
                                      @RequestParam(name = "keyword", required = false) @ModelAttribute("keyword") String keyword,
                                      Model model) {
        List<UserDisplayDto> users = new ArrayList<>();
        if (keyword != null) {
            users = userService.findUsersWithoutGroupContainKeyword(keyword);
        }
        List<GroupDto> groups = groupService.findAllGroups();
        model.addAttribute("users", users);
        model.addAttribute("groups", groups);

        return "add-to-group";
    }

    @PostMapping("/admin/groups/add")
    public String enrollUsersToGroup(@ModelAttribute("enroll") UserGroupEnrollmentDto enroll,
                                     @RequestParam(name = "keyword", required = false) @ModelAttribute String keyword,
                                     Model model, BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return groupEnrollmentForm(enroll, keyword, model);
        }
        try {
            StringBuilder message = new StringBuilder(
                    String.format("Do grupy %s zapisano następujących użytkowników: ", enroll.getGroupName()));
            for (String email : enroll.getUserEmails()) {
                userService.addUserToGroup(email, enroll.getGroupName());
                message.append(email).append(", ");
            }
            redirectAttributes.addFlashAttribute("message", message.substring(0, message.length() - 2));
        } catch (NullPointerException e) {
            bindingResult.rejectValue("userEmails", "userEmails.error",
                    "Wybierz co najmniej jednego użytkownika!");
            return groupEnrollmentForm(enroll, keyword, model);
        } catch (NoSuchGroup e) {
            bindingResult.rejectValue("userEmails", "userEmails.error",
                    e.getMessage());
            return groupEnrollmentForm(enroll, keyword, model);
        }
        return "redirect:/admin/groups/add/confirmation";
    }

    @GetMapping("/admin/groups/add/confirmation")
    public String enrollConfirmation(@ModelAttribute("message") String message){
        return "group-enroll-confirmation";
    }
}
