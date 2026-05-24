package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/my")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/courses")
    public String myCourses(Model model,
                            @AuthenticationPrincipal UserDetails userDetails,
                            RedirectAttributes ra) {
        try {
            User user = (User) userService.loadUserByUsername(userDetails.getUsername());
            model.addAttribute("courses", user.getCourses());
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Failed to load courses: " + e.getMessage());
            return "redirect:/";
        }
        return "courses/my";
    }
}
