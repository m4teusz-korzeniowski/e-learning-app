package korzeniowski.mateusz.app.web;

import korzeniowski.mateusz.app.model.course.dto.CourseDisplayDto;
import korzeniowski.mateusz.app.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
public class TeacherEditorController {
    private final CourseService courseService;

    public TeacherEditorController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/teacher/course/edit/{id}")
    public String showEditableCourse(@PathVariable long id, Model model) {
        Optional<CourseDisplayDto> course = courseService.findCourseById(id);
        course.ifPresent(courseDisplayDto -> model.addAttribute("course", courseDisplayDto));
        course.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return "course-editor";
    }
}
