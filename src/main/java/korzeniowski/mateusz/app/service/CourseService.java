package korzeniowski.mateusz.app.service;

import korzeniowski.mateusz.app.model.course.Course;
import korzeniowski.mateusz.app.model.course.dto.CourseCreationDto;
import korzeniowski.mateusz.app.model.course.dto.CourseDisplayDto;
import korzeniowski.mateusz.app.model.course.dto.CourseNameDto;
import korzeniowski.mateusz.app.model.course.dto.TeacherCourseDto;
import korzeniowski.mateusz.app.model.course.module.Module;
import korzeniowski.mateusz.app.repository.CourseRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final static int MAX_LENGTH_OF_DESCRIPTION = 20000;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseNameDto> findAllCoursesById(Long id) {
        return courseRepository.findAllById(id).stream().map(CourseNameDto::map).toList();
    }

    public List<TeacherCourseDto> findAllCoursesByTeacherId(Long id) {
        return courseRepository.findAllByCreatorId(id).stream().map(TeacherCourseDto::map).toList();
    }

    public Optional<CourseDisplayDto> findCourseById(Long id) {
        return courseRepository.findById(id).map(CourseDisplayDto::map);
    }

    public CourseNameDto findCourseNameAndCreatorNameById(Long id) {
        return courseRepository.findById(id).map(CourseNameDto::map).orElse(null);
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

    public Page<CourseNameDto> findAllCoursesPage(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return courseRepository.findAll(pageable).map(CourseNameDto::map);
    }

    public Page<CourseNameDto> findAllCoursesPageWithKeyword(String keyword, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return courseRepository.findAllByKeyword(keyword, pageable).map(CourseNameDto::map);
    }

    @Transactional
    public void removeCoursesByCreatorId(Long id) {
        courseRepository.deleteAllByCreatorId(id);
    }

    @Transactional
    public void editCourse(CourseDisplayDto courseDto) {
        Optional<Course> course = courseRepository.findById(courseDto.getId());
        if (course.isPresent()) {
            if (courseDto.getDescription().length() > MAX_LENGTH_OF_DESCRIPTION) {
                throw new DataIntegrityViolationException(
                        "*przekroczono maksymalnu rozmiar opisu!");
            }
            course.get().setDescription(courseDto.getDescription());
            courseRepository.save(course.get());
        } else {
            throw new NoSuchElementException("Nie znaleziono kursu!");
        }
    }

    @Transactional
    public void addModuleToCourse(Module module, Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            module.setCourse(course.get());
            course.get().getModules().add(module);
        } else {
            throw new NoSuchElementException("Nie znaleziono kursu!");
        }
    }

    public boolean maximumNumberOfModuleReached(Long courseId, int maxSize) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            int size = course.get().getModules().size();
            return size >= maxSize;
        } else {
            throw new NoSuchElementException("Nie znaleziono kursu!");
        }
    }

    public Long findCreatorId(Long courseId) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            return course.get().getCreatorId();
        } else {
            throw new NoSuchElementException("Nie znaleziono kursu!");
        }
    }

    public String findCourseNameById(Long courseId) {
        return courseRepository.findCourseNameById(courseId);
    }

    public Long findCreatorIdById(Long courseId) {
        return courseRepository.findCreatorIdByCourseId(courseId);
    }
}
