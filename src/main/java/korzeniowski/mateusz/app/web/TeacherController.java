package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.model.course.dto.TeacherCourseDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TeacherController {
    private final CourseService courseService;

    public TeacherController(CourseService courseService) {
        this.courseService = courseService;
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
}
