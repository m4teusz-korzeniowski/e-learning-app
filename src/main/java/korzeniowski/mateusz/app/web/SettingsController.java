package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.model.user.dto.UserSettingsDto;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController {

    private final UserService userService;

    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/settings")
    public String showSettings(Model model, HttpSession session) {
        UserSessionDto loggedInUser = (UserSessionDto) session.getAttribute("userInfo");
        try {
            UserSettingsDto user = userService.findUserSettingsById(loggedInUser.getId());
            model.addAttribute("user", user);
        } catch (UsernameNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return "settings";
    }
}
