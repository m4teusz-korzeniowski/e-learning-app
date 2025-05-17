package korzeniowski.mateusz.app.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
    ;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/demo").hasAnyRole("DEMO")
                .requestMatchers("/h2-console/**").hasAnyRole("ADMIN")
                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                .requestMatchers("/teacher/**").hasAnyRole("TEACHER")
                .requestMatchers("/").hasAnyRole("STUDENT")
                .requestMatchers("/course/**").hasAnyRole("STUDENT", "DEMO")
                .requestMatchers("/module/quiz/**").hasAnyRole("STUDENT")
                .requestMatchers("/settings").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers("/email/prepare-user").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers("/email").hasAnyRole("STUDENT", "TEACHER", "ADMIN")
                .requestMatchers("/email/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/resource/**").hasAnyRole("STUDENT", "TEACHER", "DEMO")
                .requestMatchers("/attempt/**").hasAnyRole("STUDENT", "TEACHER", "DEMO")
                .requestMatchers("/test/**").hasAnyRole("STUDENT", "TEACHER", "DEMO")
                .requestMatchers("/styles/**", "/scripts/**", "/images/**").permitAll()
                .requestMatchers("/register").permitAll()
                .anyRequest().authenticated());
        http.formLogin(login ->
                        login.loginPage("/login")
                                .successHandler(customAuthenticationSuccessHandler())
                                .failureHandler(customAuthenticationFailureHandler())
                                .permitAll())
                .sessionManagement(session -> session
                        .sessionFixation().newSession()
                        .invalidSessionUrl("/login")
                );
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout/**", HttpMethod.GET.name()))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login?logout").permitAll()
        );

        http.headers(config -> config.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            HttpSession session = request.getSession();
            if (exception instanceof DisabledException) {
                session.setAttribute("DISABLED_ERROR", "*twoje konto jest nieaktywne!");
            } else if (exception instanceof BadCredentialsException) {
                session.setAttribute("BAD_CREDENTIALS_ERROR", "*nieprawidłowy login lub hasło!");
            }
            response.sendRedirect("/login");
        };
    }

}
