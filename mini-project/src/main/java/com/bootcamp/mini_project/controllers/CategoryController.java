package com.bootcamp.mini_project.controllers;

import com.bootcamp.mini_project.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/categories")
@RestController
@Slf4j
@Tag(name = "Categories Management")
public class CategoryController {}