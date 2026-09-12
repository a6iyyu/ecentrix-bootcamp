package com.bootcamp.mini_project.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/suppliers")
@RestController
@Slf4j
@Tag(name = "Suppliers Management")
public class SupplierController {}