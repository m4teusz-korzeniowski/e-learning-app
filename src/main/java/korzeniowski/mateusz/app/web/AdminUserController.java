package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserRole;
import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;


@Controller
public class AdminUserController {
    private final UserService userService;
    private static final int PAGE_SIZE = 4;
    private final CourseService courseService;

    public AdminUserController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @GetMapping("/admin/users")
    public String showUsers(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "page", required = false) Integer currentPage,
                            @ModelAttribute("deleteMessage") String deleteMessage) {
        Page<UserDisplayDto> page;
        if (currentPage != null) {
            if (currentPage < 0) {
                currentPage = 0;
            }
            if (keyword != null) {
                page = userService.findUsersPageContainKeyword(currentPage, PAGE_SIZE, keyword);
            } else {
                page = userService.findUsersPage(currentPage, PAGE_SIZE);
            }
        } else {
            if (keyword != null) {
                page = userService.findUsersPageContainKeyword(0, PAGE_SIZE, keyword);
            } else {
                page = userService.findUsersPage(0, PAGE_SIZE);
            }
        }
        model.addAttribute("users", page.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        return "users";
    }

    @GetMapping("/admin/users/remove/{userId}")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes,
                             HttpSession session) {
        StringBuilder message = new StringBuilder();
        UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
        if (!userService.ifUserExists(userId)) {
            message.append(String.format("Użytkownik o ID %d nie istnieje!", userId));
        } else if (checkIfUserIsNotOtherAdmin(userId, userInfo.getId())) {
            if (checkIfUserIsCourseCreator(userId)) {
                courseService.removeCoursesByCreatorId(userId);
            }
            userService.removeUser(userId);
            message.append(String.format("Usunięto użytkownika o ID %d!", userId));
        } else {
            message.append("Nie możesz usunąć użytkownika, który jest adminem!");
        }
        redirectAttributes.addFlashAttribute("deleteMessage", message.toString());
        return "redirect:/admin/users";
    }

    public String editUser() {
        return "user-edit";
    }

    private boolean checkIfUserIsNotOtherAdmin(long userId, long authenticatedUserId) {
        Optional<User> user = userService.findUserById(userId);
        return user.map(value -> !value.getId().equals(authenticatedUserId)).orElse(false);
    }

    private boolean checkIfUserIsCourseCreator(long userId) {
        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            UserRole role = user.get().getUserRoles().stream().toList().get(0);
            return role.getName().equals("TEACHER");
        }
        return false;
    }
}