package korzeniowski.mateusz.app.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TeacherEditorController {

    @GetMapping("/teacher/course/edit?{id}")
    public String showEditableCourse(@PathVariable long id, Model model) {
        return "course-editor";
    }
}
