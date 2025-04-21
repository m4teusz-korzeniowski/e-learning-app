package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.exceptions.PasswordsNotMatchException;
import korzeniowski.mateusz.app.model.token.PasswordDto;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.model.user.dto.UserSettingsDto;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

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


    @GetMapping("/change-password")
    public String resetPasswordForm(Model model) {
        model.addAttribute("pass", new PasswordDto());
        return "password-change";
    }

    @PostMapping("/change-password")
    public String resetPassword(@ModelAttribute("pass") @Valid PasswordDto pass,
                                BindingResult bindingResult, Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "password-change";
        }
        try {
            Optional<User> user = userService.findUserByEmail(principal.getName());
            if (user.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            } else {
                userService.setUserPassword(user.get(), pass.getPassword(), pass.getConfirmedPassword());
            }
        } catch (PasswordsNotMatchException e) {
            bindingResult.rejectValue("confirmedPassword", "error.confirmedPassword", e.getMessage());
            return "password-change";
        }
        redirectAttributes.addFlashAttribute("message",
                "Zmiana hasła zakończyła się powodzeniem.");
        return "redirect:/settings";
    }
}
