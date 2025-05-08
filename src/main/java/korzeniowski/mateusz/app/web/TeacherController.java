package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.exceptions.EmptyQuestionBankException;
import korzeniowski.mateusz.app.model.course.dto.CourseDisplayDto;
import korzeniowski.mateusz.app.model.course.test.AttemptState;
import korzeniowski.mateusz.app.model.course.test.dto.*;
import korzeniowski.mateusz.app.model.user.dto.UserDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.*;
import korzeniowski.mateusz.app.model.course.dto.TeacherCourseDto;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    private final AttemptService attemptService;
    private final EnrollmentService enrollmentService;

    public TeacherController(CourseService courseService, AccessService accessService, UserService userService , AttemptService attemptService, EnrollmentService enrollmentService) {
        this.courseService = courseService;
        this.accessService = accessService;
        this.userService = userService;
        this.attemptService = attemptService;
        this.enrollmentService = enrollmentService;
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

    @GetMapping("/teacher/course/{courseId}/user/{userId}/results")
    public String showUserResults(@PathVariable("courseId") long courseId,
                                  @PathVariable("userId") long userId, Model model,
                                  HttpSession session) {

        String courseName = courseService.findCourseNameById(courseId);
        UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
        Long creatorId = courseService.findCreatorIdById(courseId);
        if (courseName == null || courseName.isBlank() || creatorId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if (accessService.hasLoggedInTeacherAccessToTheCourse(creatorId, userInfo.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (!enrollmentService.isUserEnrolledToCourse(userId, courseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        List<UserResultsDto> results = attemptService.findUserResultsByCourseId(courseId, userId);
        try {
            String userFullName = userService.findUserFullNameById(userId);
            model.addAttribute("userFullName", userFullName);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("userResults", results);
        model.addAttribute("courseName", courseName);
        return "user-results";
    }
}
