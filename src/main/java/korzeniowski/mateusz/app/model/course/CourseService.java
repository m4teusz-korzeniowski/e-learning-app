package korzeniowski.mateusz.app.model.course;

import korzeniowski.mateusz.app.model.course.dto.CourseCreationDto;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.course.dto.TeacherCourseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
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

    @Transactional
    public void createCourse(CourseCreationDto courseCreationDto) {
        Course course = new Course();
        course.setName(courseCreationDto.getName());
        course.setDescription(courseCreationDto.getDescription());
        course.setCreatorId(courseCreationDto.getCreatorId());
        courseRepository.save(course);
    }

    public Long findCourseIdByName(String name) {
        return courseRepository.findByName(name).map(Course::getId).
                orElseThrow(() -> new NoSuchElementException("Course not found"));
    }
}
