package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.model.course.test.dto.TestEditDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class TestEditController {

    private final TestService testService;
    private final QuestionService questionService;
    private final AccessService accessService;

    public TestEditController(TestService testService, QuestionService questionService, AccessService accessService) {
        this.testService = testService;
        this.questionService = questionService;
        this.accessService = accessService;
    }

    @GetMapping("/teacher/course/edit/{courseId}/edit-test/{testId}")
    public String showTest(@PathVariable long courseId, @PathVariable long testId, Model model,
                           HttpSession session) {
        try {
            Optional<TestEditDto> test = testService.findTestEditById(testId);
            test.ifPresent(t -> {
                model.addAttribute("test", t);
                UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
                if (accessService.hasLoggedInTeacherAccessToTheTest(testId, userInfo.getId())) {
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
            if (accessService.hasLoggedInTeacherAccessToTheTest(testId, userInfo.getId())) {
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

    @PostMapping("/teacher/course/edit/{courseId}/edit-test/{testId}/create-questions")
    public String createQuestion(@PathVariable long courseId, @PathVariable long testId,
                                 @RequestParam(name = "numberOfQuestions", required = false) Integer numberOfQuestions,
                                 @ModelAttribute("test") TestEditDto test,
                                 RedirectAttributes redirectAttributes, HttpSession session) {
        if (numberOfQuestions == null) {
            redirectAttributes.addFlashAttribute("questionMessage",
                    "*pole nie może być puste");
            return "redirect:/teacher/course/edit/" + courseId + "/edit-test/" + testId;
        } else if (numberOfQuestions < 1) {
            redirectAttributes.addFlashAttribute("questionMessage",
                    "*ilość pytań musi być równa co najmniej 1");
            return "redirect:/teacher/course/edit/" + courseId + "/edit-test/" + testId;
        }
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToTheTest(testId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            questionService.createQuestions(numberOfQuestions, testId);
            String message = "Stworzono następująca ilość pytań: " + numberOfQuestions +
                    ". Pamiętaj by je edytować w bazie pytań, zanim go udostępnisz!";
            redirectAttributes.addFlashAttribute("questionMessage", message);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/course/edit/" + courseId + "/edit-test/" + testId;
    }
}
