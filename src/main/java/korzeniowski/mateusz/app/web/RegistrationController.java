package korzeniowski.mateusz.app.web;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.exceptions.EmailAlreadyInUseException;
import korzeniowski.mateusz.app.model.user.UserService;
import korzeniowski.mateusz.app.model.user.dto.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("admin/register")
    String registration(Model model) {
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("user", user);
        return "registration-form";
    }

    @PostMapping("admin/register")
    String register(@Valid UserRegistrationDto user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getAllErrors());
            //List<ObjectError> errors = bindingResult.getAllErrors();
            //UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
            //model.addAttribute("user", userRegistrationDto);
            //model.addAttribute("errors", bindingResult);
            return registration(model);
        }
        try{
            userService.registerAppUser(user);
        }catch (EmailAlreadyInUseException ex){
            model.addAttribute("errors", ex.getMessage());
            return registration(model);
        }

        return "redirect:/admin/confirmation";
    }

    @GetMapping("admin/confirmation")
    String confirmation() {
        return "/registration-completed";
    }
}
