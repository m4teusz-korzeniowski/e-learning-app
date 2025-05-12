package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import korzeniowski.mateusz.app.exceptions.StorageException;
import korzeniowski.mateusz.app.exceptions.StorageFileNotFoundException;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleItemEditDto;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.AccessService;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.ModuleItemService;
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

import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class ModuleEditController {

    private final ModuleItemService moduleItemService;
    private final AccessService accessService;

    public ModuleEditController(ModuleItemService moduleItemService, AccessService accessService) {
        this.moduleItemService = moduleItemService;
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

    @GetMapping("/teacher/module-item/{itemId}/remove-file")
    public String removeItemModuleFile(@PathVariable long itemId, RedirectAttributes redirectAttributes) {
        try {
            moduleItemService.removeFile(itemId);
            redirectAttributes.addFlashAttribute("success", "Poprawnie usunięto plik!");
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (StorageException e) {
            redirectAttributes.addFlashAttribute("error", "*błąd operacji na pliku!");
        }
        return "redirect:/teacher/module-item/" + itemId + "/edit";
    }
}
