package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.exceptions.EmptyQuestionBankException;
import korzeniowski.mateusz.app.exceptions.ExceededTestAttemptsException;
import korzeniowski.mateusz.app.model.course.test.Attempt;
import korzeniowski.mateusz.app.model.course.test.AttemptState;
import korzeniowski.mateusz.app.model.course.test.dto.AttemptDisplayDto;
import korzeniowski.mateusz.app.model.course.test.dto.TestAttemptDto;
import korzeniowski.mateusz.app.model.course.test.dto.TestDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class TestController {

    private final TestService testService;
    private final AccessService accessService;
    private final AttemptService attemptService;
    private final CourseService courseService;

    public TestController(TestService testService, AccessService accessService, AttemptService attemptService, CourseService courseService) {
        this.testService = testService;
        this.accessService = accessService;
        this.attemptService = attemptService;
        this.courseService = courseService;
    }

    @GetMapping("/test/{testId}/display")
    public String showTest(@PathVariable("testId") Long testId, Model model, HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInUserAccessToTest(testId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            Optional<TestDisplayDto> foundTest = testService.findTestById(testId);
            if (foundTest.isPresent()) {
                String courseName = courseService.findCourseNameById(foundTest.get().getCourseId());
                model.addAttribute("currentDateTime", LocalDateTime.now());
                model.addAttribute("courseName", courseName);
                model.addAttribute("test", foundTest.get());
                List<AttemptDisplayDto> attempts = attemptService
                        .findAttemptsByUserAndTest(userInfo.getId(), foundTest.get().getId());
                model.addAttribute("attempts", attempts);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "test";
    }

    @GetMapping("/test/{testId}/attempt")
    public String prepareTestAttempt(@PathVariable("testId") long testId, HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInUserAccessToTest(testId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            Optional<TestAttemptDto> foundTest = attemptService.findTestAttempt(testId);
            if (foundTest.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            TestAttemptDto test = foundTest.get();
            if (attemptService.isAttemptInProgress(userInfo.getId(), testId)) {
                Long attemptId = attemptService.findAttemptId(userInfo.getId(), testId);
                AttemptState attemptState = attemptService.findAttemptState(attemptId);
                Integer questionNumber = attemptState.getCurrentQuestionAttempt();
                return "redirect:/attempt/" + attemptId + "/question/" + questionNumber;
            } else {
                if (attemptService.createAttemptIfAvailable(userInfo.getId(), test)) {
                    Long attemptId = attemptService.findAttemptId(userInfo.getId(), testId);
                    AttemptState attemptState = attemptService.findAttemptState(attemptId);
                    attemptService.setAttemptStartTime(test, attemptState.getLastModified());
                    test = attemptService.initializeTest(test, testId, attemptState.getId(), attemptId);
                    attemptService.updateAttemptState(attemptState.getId(), test, 1);
                    return "redirect:/attempt/" + attemptId + "/question/1";
                } else {
                    return "redirect:/test/" + testId + "/display";
                }
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (EmptyQuestionBankException | DateTimeException | ExceededTestAttemptsException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/test/" + testId + "/display";
        }
    }

    @GetMapping("/attempt/{attemptId}/question/{questionNumber}")
    public String showAttemptQuestion(@PathVariable("attemptId") long attemptId,
                                      @PathVariable("questionNumber") int questionNumber,
                                      Model model, HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInUserAccessToAttempt(attemptId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }

            TestAttemptDto attempt = getOrLoadAttemptFromSession(session, attemptId);

            if (questionNumber < 1) {
                questionNumber = 1;
            } else if (questionNumber > attempt.getNumberOfQuestions()) {
                questionNumber = attempt.getNumberOfQuestions();
            }
            model.addAttribute("totalQuestionNumber", attempt.getNumberOfQuestions());
            model.addAttribute("questionNo", questionNumber);
            model.addAttribute("attempt", attempt);
            model.addAttribute("remainingTime", attemptService.getRemainingTime(attempt));
            return "test-attempt";
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    private void checkUserAccessToAttempt(HttpSession session, Long attemptId) {
        UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
        if (accessService.hasLoggedInUserAccessToAttempt(attemptId, userInfo.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    private TestAttemptDto getOrLoadAttemptFromSession(HttpSession session, long attemptId) {
        TestAttemptDto attempt = (TestAttemptDto) session.getAttribute("attempt_" + attemptId);
        if (attempt == null) {
            AttemptState attemptState = attemptService.findAttemptState(attemptId);
            attempt = attemptService.loadAttempt(attemptState);
            session.setAttribute("attempt_" + attemptId, attempt);
        }
        return attempt;
    }


    @PostMapping("/attempt/{attemptId}/question/{questionNumber}/save")
    public Object saveAttemptAnswer(@PathVariable long attemptId,
                                    @PathVariable int questionNumber,
                                    @RequestParam("action") String action,
                                    @RequestParam(value = "answers", required = false) List<Integer> answers,
                                    HttpSession session) {
        try {
            checkUserAccessToAttempt(session, attemptId);
            TestAttemptDto attempt = getOrLoadAttemptFromSession(session, attemptId);

            if (answers != null) {
                attemptService.updateUserAnswers(attempt, questionNumber, answers);
            }
            AttemptState attemptState = attemptService.findAttemptState(attemptId);
            attemptService.updateAttemptState(attemptState.getId(), attempt, questionNumber);

            if ("previous".equals(action)) {
                if (questionNumber <= 1) {
                    questionNumber = 2;
                }
                return "redirect:/attempt/" + attemptId + "/question/" + (questionNumber - 1);
            } else if ("next".equals(action)) {
                if (questionNumber >= attempt.getNumberOfQuestions()) {
                    questionNumber = attempt.getNumberOfQuestions() - 1;
                }
                return "redirect:/attempt/" + attemptId + "/question/" + (questionNumber + 1);
            } else if ("finish".equals(action)) {
                return "redirect:/attempt/" + attemptId + "/summary";
            } else if ("autosave".equals(action)) {
                return ResponseEntity.ok().build();
            } else {
                return "redirect:/attempt/" + attemptId + "/question/" + questionNumber;
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/attempt/{attemptId}/summary")
    public String showAttemptSummary(@PathVariable("attemptId") long attemptId, HttpSession session,
                                     Model model) {
        try {
            checkUserAccessToAttempt(session, attemptId);
            TestAttemptDto attempt = getOrLoadAttemptFromSession(session, attemptId);
            model.addAttribute("attempt", attempt);
            model.addAttribute("totalQuestionNumber", attempt.getNumberOfQuestions());
            model.addAttribute("remainingTime", attemptService.getRemainingTime(attempt));
            return "attempt-summary";
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/attempt/{attemptId}/finalize")
    public String finalizeAttempt(@PathVariable("attemptId") long attemptId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            checkUserAccessToAttempt(session, attemptId);
            TestAttemptDto attempt = getOrLoadAttemptFromSession(session, attemptId);
            long testId = attempt.getTestId();
            attemptService.finishAttempt(attemptId, attempt);
            redirectAttributes.addFlashAttribute("success", "Pomyślnie zakończono test.");
            return "redirect:/test/" + testId + "/display";
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
