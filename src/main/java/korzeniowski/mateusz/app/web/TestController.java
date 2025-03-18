package korzeniowski.mateusz.app.web;

import korzeniowski.mateusz.app.model.course.test.Result;
import korzeniowski.mateusz.app.model.course.test.Test;
import korzeniowski.mateusz.app.model.course.test.TestService;
import korzeniowski.mateusz.app.model.course.test.dto.ResultDto;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.Optional;

@Controller
public class TestController {

    private final UserService userService;
    private final TestService testService;

    public TestController(UserService userService, TestService testService) {
        this.userService = userService;
        this.testService = testService;
    }

    @GetMapping("/module/quiz/{id}")
    public String showTest(@PathVariable long id, Model model, Principal principal) {
        Long userId = userService.findIdOfAuthenticatedUser(principal.getName());
        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            Test test = testService.findTestById(id);
            if(userService.ifUserHasAccessToTest(userId, id)){
                model.addAttribute("test", test);
                Optional<ResultDto> result = userService.findUserResultOfTest(userId, id);
                result.ifPresent(resultDto -> model.addAttribute("result", resultDto));
                return "test";
            }
        }
        //do poprawy
        return "redirect:/";
    }
}
