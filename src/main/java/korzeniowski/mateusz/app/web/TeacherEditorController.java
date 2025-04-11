package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.dto.CourseDisplayDto;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleDisplayDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.ModuleService;
import korzeniowski.mateusz.app.service.UserService;
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
public class TeacherEditorController {
    private final CourseService courseService;
    private final ModuleService moduleService;
    private final UserService userService;
    private final static int MAX_MODULES = 5;
    private final static int MAX_NUMBER_OF_TESTS = 3;

    public TeacherEditorController(CourseService courseService, ModuleService moduleService, UserService userService) {
        this.courseService = courseService;
        this.moduleService = moduleService;
        this.userService = userService;
    }

    @GetMapping("/teacher/course/edit/{id}")
    public String showEditableCourse(@PathVariable long id, Model model,
                                     @ModelAttribute("message") String message, HttpSession session) {
        Optional<CourseDisplayDto> course = courseService.findCourseById(id);
        course.ifPresent(courseDisplayDto -> {
            model.addAttribute("course", courseDisplayDto);
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(course.get().getCreatorId(), userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        });
        course.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("maxModules", MAX_MODULES);
        model.addAttribute("maxNumberOfTests", MAX_NUMBER_OF_TESTS);
        return "course-editor";
    }

    @PostMapping("/teacher/course/edit/{id}")
    public String editCourse(@PathVariable long id, @ModelAttribute("course") CourseDisplayDto course,
                             Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes,
                             HttpSession session) {
        if (bindingResult.hasErrors()) {
            return showEditableCourse(id, model, null, session);
        }
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(course.getCreatorId(), userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            courseService.editCourse(course);
            if (course.getModules() != null) {
                for (ModuleDisplayDto module : course.getModules()) {
                    moduleService.updateModule(module.getId(), module);
                }
            }
            redirectAttributes.addFlashAttribute("message", "Edycja zakończyła się sukcesem!");
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/course/edit/" + id;
    }

    @GetMapping("/teacher/course/edit/{courseId}/create-module")
    public String addModule(@PathVariable long courseId,
                            @ModelAttribute("module") ModuleDisplayDto module,
                            RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = moduleService.findIdOfModuleCreator(courseId);
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (courseService.maximumNumberOfModuleReached(courseId, MAX_MODULES)) {
                redirectAttributes.addFlashAttribute("message",
                        "Kurs nie może mieć więcej modułów niż " + MAX_MODULES + "!");
            } else {
                moduleService.createModule(courseId);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/course/edit/" + courseId;
    }
}
