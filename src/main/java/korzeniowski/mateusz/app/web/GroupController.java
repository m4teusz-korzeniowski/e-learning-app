package korzeniowski.mateusz.app.web;

import jakarta.validation.Valid;
import korzeniowski.mateusz.app.exceptions.NoSuchGroup;
import korzeniowski.mateusz.app.model.user.dto.GroupDto;
import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserGroupEnrollmentDto;
import korzeniowski.mateusz.app.service.GroupService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Controller
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;
    private static final int PAGE_SIZE = 2;

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
    public String createGroup(@ModelAttribute("group") @Valid GroupDto group, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return groupCreation(group);
        }
        try {
            groupService.createGroup(group);
            redirectAttributes.addFlashAttribute("message",
                    String.format("Stworzono grupę: %s", group.getName()));
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("name", "group.name.exists",
                    String.format("Grupa %s już istnieje!", group.getName()));
            return groupCreation(group);
        }
        return "redirect:/admin/groups/create/confirmation";
    }

    @GetMapping("/admin/groups/create/confirmation")
    public String groupCreationConfirmation(@ModelAttribute("message") String message) {
        return "group-creation-confirmation";
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
    public String enrollConfirmation(@ModelAttribute("message") String message) {
        return "group-enroll-confirmation";
    }

    @GetMapping("/admin/groups/display")
    public String showGroups(Model model, @RequestParam(name = "keyword", required = false) String keyword,
                             @RequestParam(value = "page", required = false) Integer currentPage,
                             @ModelAttribute("message") String message) {
        Page<GroupDto> page;
        if (currentPage != null) {
            if (keyword != null) {
                page = groupService.findAllGroupsWithPageAndKeyword(currentPage, PAGE_SIZE, keyword);
            } else {
                page = groupService.findAllGroupsWithPage(currentPage, PAGE_SIZE);
            }
        } else {
            if (keyword != null) {
                page = groupService.findAllGroupsWithPageAndKeyword(0, PAGE_SIZE, keyword);
            } else {
                page = groupService.findAllGroupsWithPage(0, PAGE_SIZE);
            }
        }
        model.addAttribute("groups", page.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        return "groups-display";
    }

    @GetMapping("/admin/groups/display/remove/{id}")
    public String deleteGroup(@PathVariable long id, RedirectAttributes redirectAttributes) {
        StringBuilder message = new StringBuilder();
        try {
            if (!groupService.ifGroupExist(id)) {
                throw new NoSuchElementException("");
            }
            groupService.removeGroup(id);
            message.append("Grupa o id = ").append(id).append(", została usunięta.");
        } catch (DataIntegrityViolationException e) {
            message.append("Nie można usunąć grupy(id = ").append(id).append("), do której należą użytkownicy!");
        } catch (NoSuchElementException e) {
            message.append("Grupa, którą chcesz usunąć nie istnieje!");
        }
        redirectAttributes.addFlashAttribute("message", message.toString());
        return "redirect:/admin/groups/display";
    }
}
