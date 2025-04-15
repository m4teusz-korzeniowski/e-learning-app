package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.model.course.dto.CourseDisplayDto;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleDisplayDto;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleItemDisplayDto;
import korzeniowski.mateusz.app.model.course.test.dto.TestNameIdDto;
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
public class TeacherEditorController {
    private final CourseService courseService;
    private final ModuleService moduleService;
    private final UserService userService;
    private final static int MAX_MODULES = 5;
    private final static int MAX_NUMBER_OF_TESTS = 3;
    private final static int MAX_NUMBER_OF_MODULE_ITEMS = 3;
    private final TestService testService;
    private final ModuleItemService moduleItemService;

    public TeacherEditorController(CourseService courseService, ModuleService moduleService, UserService userService, TestService testService, ModuleItemService moduleItemService) {
        this.courseService = courseService;
        this.moduleService = moduleService;
        this.userService = userService;
        this.testService = testService;
        this.moduleItemService = moduleItemService;
    }

    @GetMapping("/teacher/course/edit/{id}")
    public String showEditableCourse(@PathVariable long id, Model model, HttpSession session) {
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
        model.addAttribute("maxNumberOfModuleItems", MAX_NUMBER_OF_MODULE_ITEMS);
        return "course-editor";
    }

    @PostMapping("/teacher/course/edit/{id}")
    public String editCourse(@PathVariable long id, @ModelAttribute("course") @Valid CourseDisplayDto course,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes,
                             HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            String message = "*pola z nazwą nie mogą być puste!";
            model.addAttribute("message", message);
            return showEditableCourse(id, model, session);
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
                    if (module.getTests() != null) {
                        for (TestNameIdDto test : module.getTests()) {
                            testService.updateTest(test.getId(), test);
                        }
                    }
                    if (module.getItems() != null) {
                        for (ModuleItemDisplayDto moduleItem : module.getItems()) {
                            moduleItemService.updateModuleItem(moduleItem);
                        }
                    }
                }
            }
            redirectAttributes.addFlashAttribute("message", "Edycja zakończyła się sukcesem!");
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("message", e.getMessage());
            return showEditableCourse(id, model, session);
        }
        return "redirect:/teacher/course/edit/" + id;
    }

    @GetMapping("/teacher/course/edit/{courseId}/create-module")
    public String addModule(@PathVariable long courseId,
                            RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
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

    @GetMapping("/teacher/course/edit/{courseId}/remove-module/{moduleId}")
    public String removeModule(@PathVariable long courseId, @PathVariable long moduleId,
                               HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (moduleService.moduleExist(moduleId)) {
                moduleService.deleteModule(moduleId);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/course/edit/" + courseId;
    }

    @GetMapping("/teacher/course/edit/{courseId}/{moduleId}/create-test")
    public String addTest(@PathVariable long courseId, @PathVariable long moduleId,
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (moduleService.maximumNumberOfTestReached(moduleId, MAX_NUMBER_OF_TESTS)) {
                redirectAttributes.addFlashAttribute("message",
                        "Moduł nie może mieć więcej testów niż " + MAX_NUMBER_OF_TESTS + "!");
            } else {
                testService.createTest(moduleId);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/course/edit/" + courseId;
    }

    @GetMapping("/teacher/course/edit/{courseId}/remove-test/{testId}")
    public String removeTest(@PathVariable long courseId, @PathVariable long testId,
                             HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (testService.testExists(testId)) {
                testService.deleteTest(testId);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return "redirect:/teacher/course/edit/" + courseId;
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/teacher/course/edit/{courseId}/{moduleId}/create-item")
    public String addItem(@PathVariable long courseId, @PathVariable long moduleId,
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (moduleService.maximumNumberOfItemsReached(moduleId, MAX_NUMBER_OF_MODULE_ITEMS)) {
                redirectAttributes.addFlashAttribute("message",
                        "Moduł nie może mieć więcej elementów niż " + MAX_NUMBER_OF_MODULE_ITEMS + "!");
            } else {
                moduleItemService.createModuleItem(moduleId);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/course/edit/" + courseId;
    }

    @GetMapping("/teacher/course/edit/{courseId}/remove-item/{itemId}")
    public String removeItem(@PathVariable long courseId, @PathVariable long itemId,
                             HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (moduleItemService.moduleItemExists(itemId)) {
                moduleItemService.deleteModuleItem(itemId);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/course/edit/" + courseId;
    }

}
