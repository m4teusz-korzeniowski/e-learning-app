package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AdminUserController {
    private final UserService userService;
    private static final int PAGE_SIZE = 2;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public String showUsers(Principal principal, HttpSession session, Model model,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "page", required = false) Integer currentPage) {
        if (session.getAttribute("userInfo") == null) {
            userService.addUserInfoToSession(principal.getName(), session);
        }
        Page<UserDisplayDto> page;
        if (currentPage != null) {
            if (keyword != null) {
                page = userService.findUsersPageContainKeyword(currentPage, PAGE_SIZE, keyword);
                System.out.println("Case 1");
            } else {
                page = userService.findUserPage(currentPage, PAGE_SIZE);
                System.out.println("Case 2");
            }
        } else {
            if (keyword != null) {
                page = userService.findUsersPageContainKeyword(0, PAGE_SIZE, keyword);
                System.out.println("Case 3");
            }else {
                page = userService.findUserPage(0, PAGE_SIZE);
                System.out.println("Case 4");
            }
        }
        System.out.println("Ilosc stron: " + page.getTotalElements());
        model.addAttribute("users", page.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        return "users";
    }
}
