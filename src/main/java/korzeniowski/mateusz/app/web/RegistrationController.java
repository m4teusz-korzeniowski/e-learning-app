package korzeniowski.mateusz.app.web;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.exceptions.EmailAlreadyInUseException;
import korzeniowski.mateusz.app.exceptions.PeselAlreadyInUseException;
import korzeniowski.mateusz.app.service.UserService;
import korzeniowski.mateusz.app.model.user.dto.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("admin/register")
    String registration(@ModelAttribute("user") UserRegistrationDto user) {
        return "registration-form";
    }

    @PostMapping("admin/register")
    String register(@Valid @ModelAttribute("user") UserRegistrationDto user, BindingResult bindingResult,
                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return registration(user);
        }
        try {
            userService.registerAppUser(user);
        } catch (EmailAlreadyInUseException ex) {
            bindingResult.rejectValue("email", "error.email", "adres e-mail jest już w użyciu");
            return registration(user);
        } catch (PeselAlreadyInUseException ex) {
            bindingResult.rejectValue("pesel", "error.pesel", "pesel jest już w użyciu");
            return registration(user);
        }
        redirectAttributes.addFlashAttribute("message",
                String.format(
                        "Użytkownik %s %s (%s) został pomyślnie zarejestrowany"
                        , user.getFirstName(), user.getLastName(), user.getEmail()));
        return "redirect:/admin/confirmation";
    }

    @GetMapping("admin/confirmation")
    String confirmation(@ModelAttribute("message") String message) {
        return "/registration-completed";
    }
}
