package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.Note;
import com.example.demo.entity.User;
import com.example.demo.repository.NoteRepository;
import com.example.demo.service.CourseEnrollmentService;
import com.example.demo.service.CourseService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final CourseEnrollmentService enrollmentService;
    private final UserService userService;
    private final NoteRepository noteRepository;

    @GetMapping
    public String listCourses(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Course> courses = courseService.findAll();
        model.addAttribute("courses", courses);

        Map<Long, List<Note>> notesMap = new HashMap<>();
        for (Course course : courses) {
            notesMap.put(course.getId(), noteRepository.findByCourseOrderByCreatedAtDesc(course));
        }
        model.addAttribute("notesMap", notesMap);

        if (userDetails != null) {
            User user = (User) userService.loadUserByUsername(userDetails.getUsername());
            model.addAttribute("myCourses", user.getCourses());
        }
        return "courses/list";
    }

    @PostMapping("/{id}/enroll")
    public String enroll(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes ra) {
        try {
            var user = (User) userService.loadUserByUsername(userDetails.getUsername());
            var course = courseService.findById(id);
            enrollmentService.enroll(user, course);
            ra.addFlashAttribute("success", "Enrolled to " + course.getTitle());
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes ra) {
        try {
            var user = (User) userService.loadUserByUsername(userDetails.getUsername());
            var course = courseService.findById(id);
            enrollmentService.cancelEnrollment(user, course);
            ra.addFlashAttribute("success", "Canceled enrollment for " + course.getTitle());
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses";
    }

    @GetMapping("/admin/new")
    public String createForm(Model model) {
        model.addAttribute("course", new Course());
        return "courses/admin-form";
    }

    @PostMapping("/admin/save")
    public String save(@ModelAttribute Course course, RedirectAttributes ra) {
        try {
            courseService.save(course);
            ra.addFlashAttribute("success", "Course created: " + course.getTitle());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to create course: " + e.getMessage());
        }
        return "redirect:/courses";
    }

    @PostMapping("/{id}/add-note")
    public String addNote(@PathVariable Long id,
                          @RequestParam String content,
                          RedirectAttributes ra) {
        try {
            Course course = courseService.findById(id);
            Note note = new Note(content, course);
            noteRepository.save(note);
            ra.addFlashAttribute("success", "Note added");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/courses";
    }
}
