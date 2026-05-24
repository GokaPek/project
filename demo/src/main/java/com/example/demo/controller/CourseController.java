package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.example.demo.entity.User;
import com.example.demo.service.CourseEnrollmentService;
import com.example.demo.service.CourseService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final CourseEnrollmentService enrollmentService;
    private final UserService userService;

    @GetMapping
    public String listCourses(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("courses", courseService.findAll());
        if (userDetails != null) {
            User user = (User) userService.loadUserByUsername(userDetails.getUsername());
            model.addAttribute("myCourses", user.getCourses());
        }
        return "courses/list";
    }

    @PostMapping("/{id}/enroll")
    public String enroll(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        var user = (User) userService.loadUserByUsername(userDetails.getUsername());
        var course = courseService.findById(id);
        enrollmentService.enroll(user, course);
        return "redirect:/courses";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        var user = (    User) userService.loadUserByUsername(userDetails.getUsername());
        var course = courseService.findById(id);
        enrollmentService.cancelEnrollment(user, course);
        return "redirect:/courses";
    }

    // Admin endpoints
    @GetMapping("/admin/new")
    public String createForm(Model model) {
        model.addAttribute("course", new Course());
        return "courses/admin-form";
    }

    @PostMapping("/admin/save")
    public String save(@ModelAttribute Course course) {
        courseService.save(course);
        return "redirect:/courses";
    }
}
