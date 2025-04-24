package korzeniowski.mateusz.app.web;

import jakarta.servlet.http.HttpServletRequest;
import korzeniowski.mateusz.app.exceptions.StorageFileNotFoundException;
import korzeniowski.mateusz.app.model.user.dto.UserSessionDto;
import korzeniowski.mateusz.app.service.AccessService;
import korzeniowski.mateusz.app.service.CourseService;
import korzeniowski.mateusz.app.service.ModuleItemService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.NoSuchElementException;

@Controller
public class FileResourceController {
    private final AccessService accessService;
    private final ModuleItemService moduleItemService;

    public FileResourceController(AccessService accessService, ModuleItemService moduleItemService) {
        this.accessService = accessService;
        this.moduleItemService = moduleItemService;
    }

    @GetMapping("/resource/**")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(HttpServletRequest request) {

        try {
            String filePath = request.getRequestURI().substring(("/resource/").length());
            Long itemId = moduleItemService.extractItemIdFromFileName(filePath);
            UserSessionDto userInfo = (UserSessionDto) request.getSession().getAttribute("userInfo");
            if (accessService.hasLoggedInTeacherAccessToModuleItem(itemId, userInfo.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
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
