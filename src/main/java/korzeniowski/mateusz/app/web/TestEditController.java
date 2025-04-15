package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.model.course.test.dto.TestEditDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.TestService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class TestEditController {

    private final TestService testService;
    private final CourseService courseService;
    private final UserService userService;

    public TestEditController(TestService testService, CourseService courseService, UserService userService) {
        this.testService = testService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/teacher/course/edit/{courseId}/edit-test/{testId}")
    public String showTest(@PathVariable long courseId, @PathVariable long testId, Model model,
                           HttpSession session) {
        try {
            Optional<TestEditDto> test = testService.findTestEditById(testId);
            test.ifPresent(t -> {
                model.addAttribute("test", t);
                UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
                long creatorId = courseService.findCreatorId(courseId);
                if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(creatorId, userInfo.getId())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            });
            test.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "test-edit";
    }

    private String returnToEditForm(Model model, long courseId, long testId) {
        model.addAttribute("courseId", courseId);
        model.addAttribute("testId", testId);
        return "test-edit";
    }

    @PostMapping("/teacher/course/edit/{courseId}/edit-test/{testId}")
    public String editTest(@PathVariable long courseId, @PathVariable long testId, Model model,
                           HttpSession session, @ModelAttribute("test") @Valid TestEditDto test,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return returnToEditForm(model, courseId, testId);
        }
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            testService.updateTestSettings(testId, test);
            redirectAttributes.addFlashAttribute("message",
                    "Edycja testu zakończyła się sukcesem");
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("start", "error.start", e.getMessage());
            return returnToEditForm(model, courseId, testId);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("description", "error.description",
                    "*przekroczono maksymalnu rozmiar opisu");
            return returnToEditForm(model, courseId, testId);
        }
        return "redirect:/teacher/course/edit/" + courseId + "/edit-test/" + testId;
    }
}
