package com.example.demo.dto;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class CourseFilterDto {
    private String search = "";
    private Integer minStudents = null;
    private Integer maxStudents = null;
    private Integer page = 0;
    private Integer size = 9;
    private String sortBy = "id";
    private String sortDir = "asc";

    public Pageable getPageable() {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }
}
