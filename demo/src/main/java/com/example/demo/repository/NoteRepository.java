package com.example.demo.repository;

import com.example.demo.entity.Note;
import com.example.demo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByCourseOrderByCreatedAtDesc(Course course);
}
