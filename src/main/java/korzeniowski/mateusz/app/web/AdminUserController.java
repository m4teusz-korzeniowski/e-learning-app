package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class AdminUserController {
    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public String showUsers(Principal principal, HttpSession session, Model model, @Param("keyword") String keyword) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        List<UserDisplayDto> users;
        if (keyword != null) {
            users = userService.findUsersContainKeyword(keyword);
        } else {
            users = userService.findAllUsers();
        }
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "users";
    }
}
