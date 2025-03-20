package korzeniowski.mateusz.app.service;
import korzeniowski.mateusz.app.model.enroll.Enrollment;
import korzeniowski.mateusz.app.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;


@Service
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public void enrollUserToCourse(Long userId, Long courseId) {
        Enrollment enrollment = new Enrollment();
        enrollment.setUser_id(userId);
        enrollment.setCourse_id(courseId);
        enrollmentRepository.save(enrollment);
    }
}
