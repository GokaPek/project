package com.example.demo.controller;

import com.example.demo.dto.CourseDto;
import com.example.demo.dto.CourseFilterDto;
import com.example.demo.entity.Course;
import com.example.demo.entity.Note;
import com.example.demo.entity.User;
import com.example.demo.repository.NoteRepository;
import com.example.demo.service.CourseEnrollmentService;
import com.example.demo.service.CourseService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String listCourses(Model model,
                              @AuthenticationPrincipal UserDetails userDetails,
                              @ModelAttribute("filter") CourseFilterDto filter) {
        Page<CourseDto> coursePage = courseService.findAllWithFilter(filter);
        model.addAttribute("coursePage", coursePage);
        model.addAttribute("filter", filter);

        // Загружаем заметки для каждого курса
        Map<Long, List<Note>> notesMap = new HashMap<>();
        for (Course course : courseService.findAll()) {
            notesMap.put(course.getId(), noteRepository.findByCourseOrderByCreatedAtDesc(course));
        }
        model.addAttribute("notesMap", notesMap);

        if (userDetails != null) {
            User user = (User) userService.loadUserByUsername(userDetails.getUsername());
            model.addAttribute("myCourses", user.getCourses());
        }
        return "courses/list";
    }

    @GetMapping("/my")
    public String myCourses(Model model,
                            @AuthenticationPrincipal UserDetails userDetails,
                            @ModelAttribute("filter") CourseFilterDto filter) {
        User user = (User) userService.loadUserByUsername(userDetails.getUsername());
        Page<CourseDto> coursePage = courseService.findMyCourses(user, filter);
        model.addAttribute("coursePage", coursePage);
        model.addAttribute("filter", filter);
        return "courses/my";
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
        model.addAttribute("courseDto", new CourseDto());
        return "courses/admin-form";
    }

    @PostMapping("/admin/save")
    public String save(@Valid @ModelAttribute("courseDto") CourseDto courseDto,
                       BindingResult result,
                       RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "courses/admin-form";
        }
        try {
            Course course = new Course();
            course.setTitle(courseDto.getTitle());
            course.setDescription(courseDto.getDescription());
            course.setMaxStudents(courseDto.getMaxStudents());
            courseService.save(course);
            ra.addFlashAttribute("success", "Course created: " + course.getTitle());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to create course: " + e.getMessage());
        }
        return "redirect:/courses";
    }

    @GetMapping("/admin/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Course course = courseService.findById(id);
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setMaxStudents(course.getMaxStudents());
        model.addAttribute("courseDto", dto);
        return "courses/admin-form";
    }

    @PostMapping("/admin/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("courseDto") CourseDto courseDto,
                         BindingResult result,
                         RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "courses/admin-form";
        }
        try {
            courseService.update(id, courseDto);
            ra.addFlashAttribute("success", "Course updated: " + courseDto.getTitle());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to update course: " + e.getMessage());
        }
        return "redirect:/courses";
    }

    @PostMapping("/admin/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            courseService.delete(id);
            ra.addFlashAttribute("success", "Course deleted");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to delete course: " + e.getMessage());
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
