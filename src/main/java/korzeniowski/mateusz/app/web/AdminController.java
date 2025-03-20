package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserService;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String adminHome(Principal principal, HttpSession session) {
        userService.addUserInfoToSession(principal.getName(), session);
        return "admin";
    }
}
