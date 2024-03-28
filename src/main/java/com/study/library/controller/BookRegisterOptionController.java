package com.study.library.controller;

import com.study.library.service.OptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/book/option")
public class BookRegisterOptionController {

    @Autowired
    private OptionsService optionsService;

    @GetMapping("/types")
    public ResponseEntity<?> getBookType() {
        return ResponseEntity.ok(optionsService.getAllBookTypes());
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getBookCategories() {
        return ResponseEntity.ok(optionsService.getAllCategories());
    }
}
