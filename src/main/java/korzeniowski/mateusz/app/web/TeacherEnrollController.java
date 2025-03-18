package korzeniowski.mateusz.app.web;

import korzeniowski.mateusz.app.model.course.CourseService;
import korzeniowski.mateusz.app.model.course.dto.TeacherCourseEnrollDto;
import korzeniowski.mateusz.app.model.enroll.EnrollmentService;
import korzeniowski.mateusz.app.model.user.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.NoSuchElementException;

@Controller
public class TeacherEnrollController {
    private final EnrollmentService enrollmentService;
    private final CourseService courseService;
    private final UserService userService;

    public TeacherEnrollController(EnrollmentService enrollmentService, CourseService courseService, UserService userService) {
        this.enrollmentService = enrollmentService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/teacher/enroll")
    public String enrollUserForm(@ModelAttribute("enroll") TeacherCourseEnrollDto enroll) {
        return "teacher-enroll";
    }

    @PostMapping("/teacher/enroll")
    public String enrollUser(@ModelAttribute("enroll") TeacherCourseEnrollDto enroll,
                             BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return enrollUserForm(enroll);
        }
        try {
            Long userId = userService.findUserIdByEmail(enroll.getUserEmail());
            Long courseId = courseService.findCourseIdByName(enroll.getCourseName());
            enrollmentService.enrollUserToCourse(userId,courseId);
        }catch (NoSuchElementException e) {
            bindingResult.rejectValue("courseName", "error.courseName", e.getMessage());
            return enrollUserForm(enroll);
        }catch (UsernameNotFoundException e) {
            bindingResult.rejectValue("userEmail", "error.userEmail",e.getMessage());
            return enrollUserForm(enroll);
        }
        return "redirect:/teacher";
    }

}
