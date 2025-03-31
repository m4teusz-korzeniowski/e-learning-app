package korzeniowski.mateusz.app.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;

@Component
@WebFilter("/**")
public class UserSessionFilter implements Filter {
    private final UserService userService;

    public UserSessionFilter(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        if (requestURI.contains("/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userInfo = session.getAttribute("userInfo");
            if (userInfo == null) {
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    User user = (User) SecurityContextHolder
                            .getContext().getAuthentication().getPrincipal();
                    userService.addUserInfoToSession(user.getUsername(), session);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
