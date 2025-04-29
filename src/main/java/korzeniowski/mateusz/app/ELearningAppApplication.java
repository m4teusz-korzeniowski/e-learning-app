package korzeniowski.mateusz.app;

import korzeniowski.mateusz.app.file.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class ELearningAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ELearningAppApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            //do włączenia tylko po przeniesieniu prod na server
            //storageService.deleteAll();
            storageService.init();
        };
    }
}
