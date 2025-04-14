package korzeniowski.mateusz.app.model.course.module;

import jakarta.persistence.PreRemove;
import korzeniowski.mateusz.app.file.FileSystemStorageService;
import korzeniowski.mateusz.app.file.StorageService;

public class ModuleItemListener {
    private final StorageService storageService;

    public ModuleItemListener(FileSystemStorageService storageService) {
        this.storageService = storageService;
    }

    @PreRemove
    public void preRemove(ModuleItem moduleItem) {
        if (moduleItem.getFileUrl() != null && !moduleItem.getFileUrl().isBlank()) {
            storageService.delete(moduleItem.getFileUrl());
        }
    }
}
