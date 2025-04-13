package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.file.FileSystemStorageService;
import korzeniowski.mateusz.app.model.course.module.ModuleItem;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleItemDisplayDto;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleItemEditDto;
import korzeniowski.mateusz.app.repository.ModuleItemRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class ModuleItemService {
    private final ModuleItemRepository moduleItemRepository;
    private final ModuleService moduleService;
    private final FileSystemStorageService fileSystemStorageService;
    private final static String MODULE_ITEM_DIRECTORY = "module/item/";

    public ModuleItemService(ModuleItemRepository moduleItemRepository, ModuleService moduleService, FileSystemStorageService fileSystemStorageService) {
        this.moduleItemRepository = moduleItemRepository;
        this.moduleService = moduleService;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    @Transactional
    public void createModuleItem(Long moduleId) {
        ModuleItem moduleItem = new ModuleItem();
        moduleItem.setName("Nazwa elementu");
        moduleService.addItemToModule(moduleId, moduleItem);
        moduleItemRepository.save(moduleItem);
    }

    public boolean moduleItemExists(Long itemId) {
        return moduleItemRepository.existsById(itemId);
    }

    @Transactional
    public void deleteModuleItem(Long itemId) {
        moduleItemRepository.findById(itemId).ifPresent(moduleItemRepository::delete);
    }

    public void updateModuleItem(ModuleItemDisplayDto moduleItem) {
        Optional<ModuleItem> item = moduleItemRepository.findById(moduleItem.getId());
        if (item.isPresent()) {
            item.get().setName(moduleItem.getName());
            moduleItemRepository.save(item.get());
        }
    }

    public Optional<ModuleItemEditDto> findModuleItemById(Long itemId) {
        return moduleItemRepository.findById(itemId).map(ModuleItemEditDto::map);
    }

    public void updateModuleItemEditDto(ModuleItemEditDto moduleItem, MultipartFile file) {
        Optional<ModuleItem> item = moduleItemRepository.findById(moduleItem.getId());
        if (item.isPresent()) {
            item.get().setDescription(moduleItem.getDescription());
            if (file != null && !file.isEmpty()) {
                String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                String fileName = "item_" + moduleItem.getId() + "." + extension;
                String path = MODULE_ITEM_DIRECTORY + fileName;
                fileSystemStorageService.store(file, path);
                item.get().setFileUrl(file.getOriginalFilename());
            }
            moduleItemRepository.save(item.get());
        }
    }
}
