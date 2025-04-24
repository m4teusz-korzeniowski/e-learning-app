package korzeniowski.mateusz.app.web;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {

    @GetMapping("/login")
    String loginForm(HttpSession session, Model model) {
        if (session != null) {
            Object disabledError = session.getAttribute("DISABLED_ERROR");
            Object credentialsError = session.getAttribute("BAD_CREDENTIALS_ERROR");

            if (disabledError != null) {
                model.addAttribute("disabledError", disabledError);
                session.removeAttribute("DISABLED_ERROR");
            }
            if (credentialsError != null) {
                model.addAttribute("credentialsError", credentialsError);
                session.removeAttribute("BAD_CREDENTIALS_ERROR");
            }
        }

        return "login";
    }
}
