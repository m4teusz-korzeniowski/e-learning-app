package korzeniowski.mateusz.app.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserEnabledFilter extends OncePerRequestFilter {
    private final UserService userService;

    public UserEnabledFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails userDetails) {
            var email = userDetails.getUsername();
            var user = userService.findCredentialsByEmail(email);
            if (user.isPresent() && !user.get().getEnabled()) {
                request.getSession().invalidate();
                SecurityContextHolder.clearContext();
                response.sendRedirect("/login");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
