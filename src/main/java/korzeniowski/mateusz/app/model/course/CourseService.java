package korzeniowski.mateusz.app.model.course;

import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.course.dto.TeacherCourseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseNameDto> findAllCoursesById(Long id) {
        return courseRepository.findAllById(id).stream().map(CourseNameDto::map).toList();
    }

    public List<TeacherCourseDto> findAllCoursesByTeacherId(Long id) {
        return courseRepository.findAllByCreatorId(id).stream().map(TeacherCourseDto::map).toList();
    }

    public Optional<Course> findCourseById(Long id) {
        return courseRepository.findById(id);
    }
}
