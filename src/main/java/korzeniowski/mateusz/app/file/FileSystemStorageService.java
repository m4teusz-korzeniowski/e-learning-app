package korzeniowski.mateusz.app.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import korzeniowski.mateusz.app.exceptions.StorageException;
import korzeniowski.mateusz.app.exceptions.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private final List<Path> subLocations;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {

        if (properties.getLocation().trim().isEmpty()) {
            throw new StorageException("File upload location can not be Empty.");
        }

        this.rootLocation = Paths.get(properties.getLocation());
        this.subLocations = properties.getSubLocations().stream().map(Paths::get).collect(Collectors.toList());
    }

    @Override
    public void store(MultipartFile file, String filePath) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path relative = Paths.get(filePath).normalize();
            if (Files.isDirectory(relative) || relative.getFileName() == null) {
                throw new StorageException("Invalid file path. It must include a file name.");
            }
            Path destinationFile = this.rootLocation
                    .resolve(relative)
                    .normalize()
                    .toAbsolutePath();
            boolean isAllowed = subLocations.stream().anyMatch(relative::startsWith);
            if (!isAllowed) {
                throw new StorageException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.createDirectories(destinationFile.getParent());
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }


    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            for (Path path : subLocations) {
                Files.createDirectories(rootLocation.resolve(path));
            }
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void delete(String filename) {
        try {
            Path file = rootLocation.resolve(filename).normalize().toAbsolutePath();
            if (!file.startsWith(rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot delete file outside current directory.");
            }
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException("Failed to delete file: " + filename, e);
        }
    }

    @Override
    public void deleteAllStartsWith(String directory, String prefix) {
        Path path = rootLocation.resolve(directory).normalize().toAbsolutePath();
        if (!path.startsWith(rootLocation.toAbsolutePath())) {
            throw new StorageException("Cannot delete file outside current directory.");
        }
        File dir = path.toFile();
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] matchingFiles = dir.listFiles((dir1, name) -> name.startsWith(prefix));
        if (matchingFiles != null) {
            for (File file : matchingFiles) {
                file.delete();
            }
        }
    }
}