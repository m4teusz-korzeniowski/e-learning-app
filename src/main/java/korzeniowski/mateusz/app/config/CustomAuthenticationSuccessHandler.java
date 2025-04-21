package korzeniowski.mateusz.app.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String redirect = request.getContextPath();
        if (authentication.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"))) {
            redirect = "/admin";
        } else if (authentication.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_STUDENT"))) {
            redirect = "/";
        } else if (authentication.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_TEACHER"))) {
            redirect = "/teacher";
        }
        response.sendRedirect(redirect);
    }
}
