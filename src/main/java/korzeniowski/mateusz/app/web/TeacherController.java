package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.exceptions.EmptyQuestionBankException;
import korzeniowski.mateusz.app.model.course.dto.CourseDisplayDto;
import korzeniowski.mateusz.app.model.course.test.AttemptState;
import korzeniowski.mateusz.app.model.course.test.dto.AttemptStateDto;
import korzeniowski.mateusz.app.model.course.test.dto.QuestionAttemptDto;
import korzeniowski.mateusz.app.model.course.test.dto.TestAttemptDto;
import korzeniowski.mateusz.app.model.course.test.dto.TestDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.*;
import korzeniowski.mateusz.app.model.course.dto.TeacherCourseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class TeacherController {
    private final CourseService courseService;
    private final AccessService accessService;
    private final UserService userService;
    private final static int PAGE_SIZE = 10;
    private final TestService testService;
    private final AttemptService attemptService;

    public TeacherController(CourseService courseService, AccessService accessService, UserService userService, TestService testService, AttemptService attemptService) {
        this.courseService = courseService;
        this.accessService = accessService;
        this.userService = userService;
        this.testService = testService;
        this.attemptService = attemptService;
    }

    @GetMapping("/teacher")
    public String teacherHome(Model model) {
        return "teacher";
    }

    @GetMapping("/teacher/course")
    public String teacherCourse(Model model, HttpSession session) {
        UserSessionDto user = (UserSessionDto) session.getAttribute("userInfo");
        Long teacherId = user.getId();
        List<TeacherCourseDto> courses = courseService.findAllCoursesByTeacherId(teacherId);
        model.addAttribute("courses", courses);
        return "teacher-course";
    }

    @GetMapping("/teacher/course/{courseId}/display")
    public String displayCourse(@PathVariable("courseId") Long courseId, Model model,
                                HttpSession session) {
        Optional<CourseDisplayDto> foundCourse = courseService.findCourseById(courseId);
        if (foundCourse.isPresent()) {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            Long creatorId = foundCourse.get().getCreatorId();
            if (accessService.hasLoggedInTeacherAccessToTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            model.addAttribute("course", foundCourse.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "teacher-course-display";
    }

    @GetMapping("/teacher/course/{courseId}/users")
    public String showUsers(@PathVariable("courseId") long courseId, Model model,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "page", required = false) Integer currentPage,
                            @ModelAttribute("deleteMessage") String deleteMessage,
                            HttpSession session) {

        String courseName = courseService.findCourseNameById(courseId);
        Long creatorId = courseService.findCreatorIdById(courseId);
        UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
        if (courseName == null || courseName.isBlank() || creatorId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (accessService.hasLoggedInTeacherAccessToTheCourse(creatorId, userInfo.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        model.addAttribute("courseName", courseName);
        Page<UserDisplayDto> page;
        if (currentPage != null) {
            if (currentPage < 0) {
                currentPage = 0;
            }
            if (keyword != null) {
                page = userService.findCourseParticipantsContainKeyword(currentPage, PAGE_SIZE, keyword, courseId);
            } else {
                page = userService.findCourseParticipants(currentPage, PAGE_SIZE, courseId);
            }
        } else {
            if (keyword != null) {
                page = userService.findCourseParticipantsContainKeyword(0, PAGE_SIZE, keyword, courseId);
            } else {
                page = userService.findCourseParticipants(0, PAGE_SIZE, courseId);
            }
        }
        model.addAttribute("users", page.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        return "course-participants";
    }

    @GetMapping("/teacher/test/{testId}/display")
    public String showTest(@PathVariable("testId") Long testId, Model model, HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToTheTest(testId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            Optional<TestDisplayDto> foundTest = testService.findTestById(testId);
            if (foundTest.isPresent()) {
                String courseName = courseService.findCourseNameById(foundTest.get().getCourseId());
                model.addAttribute("currentDateTime", LocalDateTime.now());
                model.addAttribute("courseName", courseName);
                model.addAttribute("test", foundTest.get());
                model.addAttribute("attempts", new ArrayList<>());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "test-teacher";
    }

    @GetMapping("/teacher/test/{testId}/attempt")
    public String showTestAttempt(@PathVariable("testId") long testId, HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToTheTest(testId, userInfo.getId())) {
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
                test = attemptService.loadAttempt(attemptState);
                Integer questionNumber = attemptState.getCurrentQuestionAttempt();
                return "redirect:/teacher/attempt/" + attemptId + "/question/" + questionNumber;
            } else {
                if (attemptService.createAttemptIfAvailable(userInfo.getId(), test)) {
                    Long attemptId = attemptService.findAttemptId(userInfo.getId(), testId);
                    AttemptState attemptState = attemptService.findAttemptState(attemptId);
                    test = attemptService.initializeTest(test, testId, attemptState.getId(), attemptId);
                    attemptService.updateAttemptState(attemptState.getId(), test);
                    return "redirect:/teacher/attempt/" + attemptId + "/question/1";
                } else {
                    redirectAttributes.addFlashAttribute("error", "*przekroczono limit podejść do testu!");
                    return "redirect:/teacher/test/" + testId + "/display";
                }
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (EmptyQuestionBankException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/teacher/test/" + testId + "/display";
        }
    }

    @GetMapping("/teacher/attempt/{attemptId}/question/{questionNumber}")
    public String showAttemptQuestion(@PathVariable("attemptId") long attemptId,
                                      @PathVariable("questionNumber") int questionNumber,
                                      Model model, HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToTheTest(attemptId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            AttemptState attemptState = attemptService.findAttemptState(attemptId);
            TestAttemptDto attempt = attemptService.loadAttempt(attemptState);
            model.addAttribute("attempt", attempt);
            //model.addAttribute("attemptStateId", attemptState);
            //model.addAttribute("questionNumber", questionNumber);
            return "test-attempt";
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public String testAttempt() {
        return "";
    }
}
