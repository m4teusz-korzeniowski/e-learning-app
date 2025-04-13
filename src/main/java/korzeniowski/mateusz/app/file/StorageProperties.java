package korzeniowski.mateusz.app.file;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("storage")
public class StorageProperties {

    private String location = "upload-dir";
    private List<String> subLocations = List.of("module/item", "module/task");

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getSubLocations() {
        return subLocations;
    }

    public void setSubLocations(List<String> subLocations) {
        this.subLocations = subLocations;
    }
}
