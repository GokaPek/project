package com.example.demo.repository;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Course> findByUsersContaining(User user, Pageable pageable);

    @Query("SELECT c FROM Course c WHERE " +
            "(:search IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:minStudents IS NULL OR c.maxStudents >= :minStudents) AND " +
            "(:maxStudents IS NULL OR c.maxStudents <= :maxStudents)")
    Page<Course> findByFilter(@Param("search") String search,
                              @Param("minStudents") Integer minStudents,
                              @Param("maxStudents") Integer maxStudents,
                              Pageable pageable);
}