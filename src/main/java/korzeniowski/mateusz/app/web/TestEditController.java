package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionsCreationDto;
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

    private void addTestToModel(long testId, Model model, HttpSession session) {
        model.addAttribute("question", new QuestionsCreationDto());
        Optional<TestEditDto> test = testService.findTestEditById(testId);
        test.ifPresent(t -> {
            model.addAttribute("test", t);
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToTheTest(testId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        });
        test.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/teacher/test/{testId}/edit")
    public String showTest(@PathVariable long testId, Model model,
                           HttpSession session) {
        try {
            addTestToModel(testId, model, session);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "test-edit";
    }

    private String returnToEditForm(Model model, long testId, TestEditDto test) {
        model.addAttribute("question", new QuestionsCreationDto());
        model.addAttribute("testId", testId);
        model.addAttribute("test", test);
        return "test-edit";
    }

    @PostMapping("/teacher/test/{testId}/edit")
    public String editTest(@PathVariable long testId, Model model,
                           HttpSession session, @ModelAttribute("test") @Valid TestEditDto test,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return returnToEditForm(model, testId, test);
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
            return returnToEditForm(model, testId, test);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("description", "error.description",
                    "*przekroczono maksymalnu rozmiar opisu");
            return returnToEditForm(model, testId, test);
        }
        return "redirect:/teacher/test/" + testId + "/edit";
    }

    @PostMapping("/teacher/test/{testId}/create-questions")
    public String createQuestion(@PathVariable long testId,
                                 @ModelAttribute("question") @Valid QuestionsCreationDto question,
                                 BindingResult bindingResult, Model model,
                                 RedirectAttributes redirectAttributes, HttpSession session) {
        if (bindingResult.hasErrors()) {
            ;
            String error = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/teacher/test/" + testId + "/edit";
        }
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToTheTest(testId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            questionService.createQuestions(question.getNumberOfQuestions(), testId);
            String message = "Stworzono następująca ilość pytań: " + question.getNumberOfQuestions() +
                    ". Pamiętaj by je edytować w bazie pytań, zanim go udostępnisz!";
            redirectAttributes.addFlashAttribute("questionMessage", message);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/test/" + testId + "/edit";
    }
}
