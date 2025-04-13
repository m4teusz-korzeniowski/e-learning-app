package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleItemEditDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.ModuleItemService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class ModuleEditController {

    private final ModuleItemService moduleItemService;
    private final CourseService courseService;
    private final UserService userService;

    public ModuleEditController(ModuleItemService moduleItemService, CourseService courseService, UserService userService) {
        this.moduleItemService = moduleItemService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/teacher/course/edit/{courseId}/edit-item/{itemId}")
    public String showEditModule(@PathVariable long courseId, @PathVariable long itemId,
                                 Model model, HttpSession session,
                                 @ModelAttribute("message") String message) {
        Optional<ModuleItemEditDto> moduleItem = moduleItemService.findModuleItemById(itemId);
        moduleItem.ifPresent(item -> {
            model.addAttribute("moduleItem", item);
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        });
        moduleItem.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return "module-item-edit";
    }

    @PostMapping("/teacher/course/edit/{courseId}/edit-item/{itemId}")
    public String editModule(@PathVariable long courseId, @PathVariable long itemId,
                             @ModelAttribute("moduleItem") ModuleItemEditDto item,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
            if (!userService.ifLoggedInTeacherIsOwnerOfTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            moduleItemService.updateModuleItemEditDto(item, file);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return "redirect:/teacher/course/edit/" + courseId + "/edit-item/" + itemId;
    }
}
