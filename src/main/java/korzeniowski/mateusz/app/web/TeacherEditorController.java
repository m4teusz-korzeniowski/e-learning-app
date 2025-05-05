package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import korzeniowski.mateusz.app.exceptions.AttemptInProgressException;
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
    private final static int MAX_MODULES = 5;
    private final static int MAX_NUMBER_OF_TESTS = 3;
    private final static int MAX_NUMBER_OF_MODULE_ITEMS = 3;
    private final TestService testService;
    private final ModuleItemService moduleItemService;
    private final AccessService accessService;

    public TeacherEditorController(CourseService courseService, ModuleService moduleService, TestService testService, ModuleItemService moduleItemService, AccessService accessService) {
        this.courseService = courseService;
        this.moduleService = moduleService;
        this.testService = testService;
        this.moduleItemService = moduleItemService;
        this.accessService = accessService;
    }

    @GetMapping("/teacher/course/edit/{id}")
    public String showEditableCourse(@PathVariable long id, Model model, HttpSession session) {
        Optional<CourseDisplayDto> foundCourse = courseService.findCourseById(id);
        foundCourse.ifPresent(course -> {
            model.addAttribute("course", course);
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToTheCourse(foundCourse.get().getCreatorId(), userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        });
        foundCourse.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
            if (accessService.hasLoggedInTeacherAccessToTheCourse(course.getCreatorId(), userInfo.getId())) {
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
            if (accessService.hasLoggedInTeacherAccessToTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            if (courseService.maximumNumberOfModuleReached(courseId, MAX_MODULES)) {
                redirectAttributes.addFlashAttribute("message",
                        "*kurs nie może mieć więcej modułów niż " + MAX_MODULES + "!");
            } else {
                moduleService.createModule(courseId);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/course/edit/" + courseId;
    }

    @GetMapping("/teacher/module/{moduleId}/remove")
    public String removeModule(@PathVariable long moduleId,
                               HttpSession session, RedirectAttributes redirectAttributes) {
        Long courseId = null;
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToModule(moduleId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            courseId = moduleService.findCourseIdFromModule(moduleId);
            if (moduleService.moduleExist(moduleId)) {
                moduleService.deleteModule(moduleId);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (AttemptInProgressException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/teacher/course/edit/" + courseId;
        }
        return "redirect:/teacher/course/edit/" + courseId;
    }

    @GetMapping("/teacher/module/{moduleId}/create-test")
    public String addTest(@PathVariable long moduleId,
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToModule(moduleId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            long courseId = moduleService.findCourseIdFromModule(moduleId);
            if (moduleService.maximumNumberOfTestReached(moduleId, MAX_NUMBER_OF_TESTS)) {
                redirectAttributes.addFlashAttribute("error",
                        "*moduł nie może mieć więcej testów niż " + MAX_NUMBER_OF_TESTS + "!");
            } else {
                testService.createTest(moduleId);
            }
            return "redirect:/teacher/course/edit/" + courseId;
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/teacher/test/{testId}/remove")
    public String removeTest(@PathVariable long testId,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        Long courseId = null;
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToTheTest(testId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            courseId = testService.findCourseIdFromTest(testId);
            if (testService.testExists(testId)) {
                testService.deleteTest(testId);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            return "redirect:/teacher/course/edit/" + courseId;
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (AttemptInProgressException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/teacher/course/edit/" + courseId;
        }
    }

    @GetMapping("/teacher/module/{moduleId}/create-item")
    public String addItem(@PathVariable long moduleId,
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
        Long courseId = null;
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToModule(moduleId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            courseId = moduleService.findCourseIdFromModule(moduleId);
            if (moduleService.maximumNumberOfItemsReached(moduleId, MAX_NUMBER_OF_MODULE_ITEMS)) {
                redirectAttributes.addFlashAttribute("error",
                        "*moduł nie może mieć więcej elementów niż " + MAX_NUMBER_OF_MODULE_ITEMS + "!");
            } else {
                moduleItemService.createModuleItem(moduleId);
            }
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/course/edit/" + courseId;
    }

    @GetMapping("/teacher/module-item/{itemId}/remove")
    public String removeItem(@PathVariable long itemId,
                             HttpSession session) {
        Long courseId = null;
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToModuleItem(itemId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            courseId = moduleItemService.findCourseIdFromModuleItem(itemId);
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
