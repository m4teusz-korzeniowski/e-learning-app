package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.test.dto.TestDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.TestService;
import korzeniowski.mateusz.app.model.course.test.dto.ResultDto;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

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
    public String showTest(@PathVariable long id, Model model, HttpSession session) {
        UserSessionDto userSessionDto = (UserSessionDto) session.getAttribute("userInfo");
        Long userId = userSessionDto.getId();
        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            Optional<TestDisplayDto> test = testService.findTestById(id);
            if (test.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            if (userService.ifUserHasAccessToTest(userId, id)) {
                test.ifPresent(testDisplayDto -> model.addAttribute("test", testDisplayDto));
                Optional<ResultDto> result = userService.findUserResultOfTest(userId, id);
                result.ifPresent(resultDto -> model.addAttribute("result", resultDto));
                return "test";
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
