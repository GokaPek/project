package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CourseDto {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Max students is required")
    @Min(value = 1, message = "Max students must be at least 1")
    @Max(value = 100, message = "Max students cannot exceed 100")
    private Integer maxStudents = 30;

    private Integer enrolledCount;
    private Boolean available;
}
