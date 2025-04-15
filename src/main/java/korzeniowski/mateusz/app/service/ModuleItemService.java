package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.file.FileSystemStorageService;
import korzeniowski.mateusz.app.file.StorageService;
import korzeniowski.mateusz.app.model.course.module.ModuleItem;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleItemDisplayDto;
import korzeniowski.mateusz.app.model.course.module.dto.ModuleItemEditDto;
import korzeniowski.mateusz.app.repository.ModuleItemRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class ModuleItemService {
    private final ModuleItemRepository moduleItemRepository;
    private final ModuleService moduleService;
    private final StorageService storageService;
    private final static String MODULE_ITEM_DIRECTORY = "module/item/";
    private final static int MAX_LENGTH_OF_ITEM_NAME = 60;

    public ModuleItemService(ModuleItemRepository moduleItemRepository, ModuleService moduleService, FileSystemStorageService storageService) {
        this.moduleItemRepository = moduleItemRepository;
        this.moduleService = moduleService;
        this.storageService = storageService;
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
            if(moduleItem.getName().length() > MAX_LENGTH_OF_ITEM_NAME) {
                throw new DataIntegrityViolationException("*przekroczono rozmiar dla nazwy elementu modułu!");
            }
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
                storageService.deleteAllStartsWith(MODULE_ITEM_DIRECTORY, "item_" + moduleItem.getId() + ".");
                storageService.store(file, path);
                item.get().setFileUrl(path);
            }
            moduleItemRepository.save(item.get());
        }
    }

    public Resource getFile(String fileName) {
        return storageService.loadAsResource(fileName);
    }
}
