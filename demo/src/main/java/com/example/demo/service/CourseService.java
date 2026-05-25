package com.example.demo.service;

import com.example.demo.dto.CourseDto;
import com.example.demo.dto.CourseFilterDto;
import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Page<CourseDto> findAllWithFilter(CourseFilterDto filter) {
        Page<Course> coursePage;

        if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
            coursePage = courseRepository.findByTitleContainingIgnoreCase(
                    filter.getSearch(), filter.getPageable());
        } else {
            coursePage = courseRepository.findAll(filter.getPageable());
        }

        List<CourseDto> dtos = coursePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, filter.getPageable(), coursePage.getTotalElements());
    }

    public Page<CourseDto> findMyCourses(User user, CourseFilterDto filter) {
        Page<Course> coursePage = courseRepository.findByUsersContaining(
                user, filter.getPageable());

        List<CourseDto> dtos = coursePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, filter.getPageable(), coursePage.getTotalElements());
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public Course update(Long id, CourseDto dto) {
        Course course = findById(id);
        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setMaxStudents(dto.getMaxStudents());
        return courseRepository.save(course);
    }

    public void delete(Long id) {
        courseRepository.deleteById(id);
    }

    private CourseDto convertToDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setMaxStudents(course.getMaxStudents());
        dto.setEnrolledCount(course.getEnrolledCount());
        dto.setAvailable(course.isAvailable());
        return dto;
    }
}