package korzeniowski.mateusz.app.web;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.exceptions.EmailAlreadyInUseException;
import korzeniowski.mateusz.app.service.UserService;
import korzeniowski.mateusz.app.model.user.dto.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;


@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("admin/register")
    String registration(@ModelAttribute("user") UserRegistrationDto user, Principal principal, HttpSession session) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        return "registration-form";
    }

    @PostMapping("admin/register")
    String register(@Valid @ModelAttribute("user") UserRegistrationDto user, BindingResult bindingResult,
                    HttpSession session, Principal principal) {
        if (bindingResult.hasErrors()) {
            return registration(user, principal, session);
        }
        try {
            userService.registerAppUser(user);
        } catch (EmailAlreadyInUseException ex) {
            bindingResult.rejectValue("email", "error.email", "email is already in use");
            return registration(user, principal, session);
        }
        return confirmation(user, session, principal);
        //return "redirect:/admin/confirmation";
    }

    @GetMapping("admin/confirmation")
    String confirmation(@ModelAttribute UserRegistrationDto user, HttpSession session, Principal principal) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        return "/registration-completed";
    }
}
