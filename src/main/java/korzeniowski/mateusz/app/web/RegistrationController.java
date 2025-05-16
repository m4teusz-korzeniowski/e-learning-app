package korzeniowski.mateusz.app.web;

import jakarta.validation.Valid;
import korzeniowski.mateusz.app.exceptions.EmailAlreadyInUseException;
import korzeniowski.mateusz.app.exceptions.PasswordsNotMatchException;
import korzeniowski.mateusz.app.exceptions.PeselAlreadyInUseException;
import korzeniowski.mateusz.app.model.token.PasswordDto;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.service.PasswordTokenService;
import korzeniowski.mateusz.app.service.UserService;
import korzeniowski.mateusz.app.model.user.dto.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;


@Controller
public class RegistrationController {

    private final UserService userService;
    private final PasswordTokenService passwordTokenService;
    private final static String INVALID_TOKEN_MESSAGE = "*token rejestracyjny jest nieprawidłowy lub wygasł. W razie problemów" +
            " skontaktuj się z administracją.";

    public RegistrationController(UserService userService, PasswordTokenService passwordTokenService) {
        this.userService = userService;
        this.passwordTokenService = passwordTokenService;
    }

    @GetMapping("/admin/register")
    public String registration(@ModelAttribute("user") UserRegistrationDto user) {
        return "registration-form";
    }

    @PostMapping("/admin/register")
    public String register(@Valid @ModelAttribute("user") UserRegistrationDto user, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes, Principal principal) {
        if (bindingResult.hasErrors()) {
            return registration(user);
        }
        try {
            userService.registerAppUser(user, principal.getName());
        } catch (EmailAlreadyInUseException ex) {
            bindingResult.rejectValue("email", "error.email", "*adres e-mail jest już w użyciu");
            return registration(user);
        } catch (PeselAlreadyInUseException ex) {
            bindingResult.rejectValue("pesel", "error.pesel", "*pesel jest już w użyciu");
            return registration(user);
        }
        redirectAttributes.addFlashAttribute("message",
                String.format(
                        "Użytkownik %s %s (%s) został pomyślnie zarejestrowany"
                        , user.getFirstName(), user.getLastName(), user.getEmail()));
        return "redirect:/admin/confirmation";
    }

    @GetMapping("/admin/confirmation")
    public String confirmation(@ModelAttribute("message") String message) {
        return "registration-completed";
    }

    @GetMapping("/register")
    public String finishRegistration(@RequestParam String token, Model model) {
        Optional<User> user = passwordTokenService.findUserByToken(token);
        if (user.isEmpty()) {
            model.addAttribute("message", INVALID_TOKEN_MESSAGE);
            return "register-failed";
        }
        model.addAttribute("token", token);
        model.addAttribute("pass", new PasswordDto());
        return "register-finish";
    }

    @PostMapping("/register")
    public String finishRegistration(@RequestParam String token,
                                     @ModelAttribute("pass") @Valid PasswordDto pass,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        Optional<User> user = passwordTokenService.findUserByToken(token);
        if (user.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", INVALID_TOKEN_MESSAGE);
            return "redirect:/register?token=" + token;
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("token", token);
            return "register-finish";
        }
        try {
            userService.setUserPassword(user.get(), pass.getPassword(), pass.getConfirmedPassword());
            user.get().setEnabled(true);
            passwordTokenService.deleteToken(token);
        } catch (PasswordsNotMatchException e) {
            bindingResult.rejectValue("confirmedPassword", "error.confirmedPassword", e.getMessage());
            model.addAttribute("token", token);
            return "register-finish";
        }
        redirectAttributes.addFlashAttribute("success",
                "Udało się ustawić hasło! Możesz się teraz zalogować.");
        return "redirect:/login";
    }
}
