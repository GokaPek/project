package com.example.demo.service;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseEnrollmentService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void enroll(User user, Course course) {
        if (user.getCourses().contains(course)) {
            throw new RuntimeException("Already enrolled");
        }
        if (!course.isAvailable()) {
            throw new RuntimeException("Course is full");
        }
        user.getCourses().add(course);
        userRepository.save(user);
    }

    @Transactional
    public void cancelEnrollment(User user, Course course) {
        user.getCourses().remove(course);
        userRepository.save(user);
    }

    public boolean isEnrolled(User user, Course course) {
        return user.getCourses().contains(course);
    }

    public Set<Course> getCoursesForUser(User user) {
        return user.getCourses();
    }
}
