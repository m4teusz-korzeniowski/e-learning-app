package korzeniowski.mateusz.app.web;

import korzeniowski.mateusz.app.model.user.UserService;
import korzeniowski.mateusz.app.model.user.dto.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("admin/register")
    String registration(Model model){
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("user", user);
        return "registration-form";
    }

    @PostMapping("admin/register")
    String register(UserRegistrationDto user) {
        userService.registerAppUser(user);
        return "redirect:/admin/confirmation";
    }

    @GetMapping("admin/confirmation")
    String confirmation() {
        return "/registration-completed";
    }
}
