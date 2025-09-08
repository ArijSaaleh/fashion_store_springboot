package com.arij.fashionecommerce.controller;

import com.arij.fashionecommerce.model.Category;
import com.arij.fashionecommerce.repository.CategoryRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository repo;
    public CategoryController(CategoryRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Category> all() {
        return repo.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Category> create(@Valid @RequestBody Category c) {
        if (repo.existsByNameIgnoreCase(c.getName())) {
            return ResponseEntity.badRequest().build();
        }
        Category saved = repo.save(c);
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId())).body(saved);
    }
}