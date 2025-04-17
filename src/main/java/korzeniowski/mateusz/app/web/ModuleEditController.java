package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.exceptions.StorageFileNotFoundException;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleItemEditDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.AccessService;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.ModuleItemService;
import korzeniowski.mateusz.app.service.UserService;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class ModuleEditController {

    private final ModuleItemService moduleItemService;
    private final CourseService courseService;
    private final AccessService accessService;

    public ModuleEditController(ModuleItemService moduleItemService, CourseService courseService, AccessService accessService) {
        this.moduleItemService = moduleItemService;
        this.courseService = courseService;
        this.accessService = accessService;
    }

    @GetMapping("/teacher/module-item/{itemId}/edit")
    public String showEditModule(@PathVariable long itemId,
                                 Model model, HttpSession session,
                                 @ModelAttribute("message") String message) {
        try {
            Optional<ModuleItemEditDto> moduleItem = moduleItemService.findModuleItemById(itemId);
            moduleItem.ifPresent(item -> {
                model.addAttribute("moduleItem", item);
                UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
                if (accessService.hasLoggedInTeacherAccessToModuleItem(itemId, userInfo.getId())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            });
            moduleItem.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return "module-item-edit";
    }

    private String returnEditForm(long itemId, Model model, ModuleItemEditDto item) {
        model.addAttribute("moduleItem", item);
        model.addAttribute("itemId", itemId);
        return "module-item-edit";
    }

    @PostMapping("/teacher/module-item/{itemId}/edit")
    public String editModule(@PathVariable long itemId,
                             @ModelAttribute("moduleItem") ModuleItemEditDto item,
                             BindingResult bindingResult, Model model,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return returnEditForm(itemId, model, item);
        }
        try {
            UserSessionDto userInfo = (UserSessionDto) session.getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToModuleItem(itemId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            moduleItemService.updateModuleItemEditDto(item, file);
            redirectAttributes.addFlashAttribute("message", "Edycja zakończyła się powodzeniem!");
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("description", "error.description",
                    "*przekroczono maksymalnu rozmiar opisu");
            return returnEditForm(itemId, model, item);
        }
        return "redirect:/teacher/module-item/" + itemId + "/edit";
    }

    @GetMapping("/teacher/{courseId}/files/**")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(HttpServletRequest request, @PathVariable long courseId) {

        try {
            UserSessionDto userInfo = (UserSessionDto) request.getSession().getAttribute("userInfo");
            long creatorId = courseService.findCreatorId(courseId);
            if (accessService.hasLoggedInTeacherAccessToTheCourse(creatorId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
            String filePath = request.getRequestURI().substring(("/teacher/" + courseId + "/files/").length());
            Resource file = moduleItemService.getFile(filePath);
            if (file == null)
                return ResponseEntity.notFound().build();

            String contentType = Files.probeContentType(file.getFile().toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            if (contentType.startsWith("image/") || contentType.equals("application/pdf")
                    || contentType.equals("text/plain") || contentType.equals("text/html")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(file);
            } else {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + file.getFilename() + "\"")
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(file);
            }
        } catch (StorageFileNotFoundException | NoSuchElementException | IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
