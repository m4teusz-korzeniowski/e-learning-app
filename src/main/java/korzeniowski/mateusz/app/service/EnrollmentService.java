package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.exceptions.StudentRoleMissingException;
import korzeniowski.mateusz.app.model.enroll.Enrollment;
import korzeniowski.mateusz.app.model.user.User;
import korzeniowski.mateusz.app.model.user.UserRole;
import korzeniowski.mateusz.app.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;


@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, UserService userService) {
        this.enrollmentRepository = enrollmentRepository;
        this.userService = userService;
    }

    public void enrollUserToCourse(Long userId, Long courseId) {
        Optional<User> user = userService.findUserById(userId);
        if (user.isPresent()) {
            Stream<String> stream = user.get().getUserRoles().stream().map(UserRole::getName);
            for (String role : stream.toList()) {
                if (!role.equals("STUDENT")) {
                    throw new StudentRoleMissingException
                            ("Użytkownik, którego chcesz zapisać nie ma uprawnień ucznia!");
                }
            }
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setUser_id(userId);
        enrollment.setCourse_id(courseId);
        enrollmentRepository.save(enrollment);
    }
}
