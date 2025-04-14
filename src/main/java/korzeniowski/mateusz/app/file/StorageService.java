package korzeniowski.mateusz.app.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {

    void init();

    void store(MultipartFile file, String filePath);

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    void delete(String filename);

    void deleteAllStartsWith(String directory, String prefix);
}
