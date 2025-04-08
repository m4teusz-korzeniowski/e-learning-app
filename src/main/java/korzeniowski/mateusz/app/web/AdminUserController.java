package korzeniowski.mateusz.app.web;

import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AdminUserController {
    private final UserService userService;
    private static final int PAGE_SIZE = 4;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/users")
    public String showUsers(Model model, @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "page", required = false) Integer currentPage) {
        Page<UserDisplayDto> page;
        if (currentPage != null) {
            if (currentPage < 0) {
                currentPage = 0;
            }
            if (keyword != null) {
                page = userService.findUsersPageContainKeyword(currentPage, PAGE_SIZE, keyword);
            } else {
                page = userService.findUsersPage(currentPage, PAGE_SIZE);
            }
        } else {
            if (keyword != null) {
                page = userService.findUsersPageContainKeyword(0, PAGE_SIZE, keyword);
            } else {
                page = userService.findUsersPage(0, PAGE_SIZE);
            }
        }
        model.addAttribute("users", page.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        return "users";
    }
}
