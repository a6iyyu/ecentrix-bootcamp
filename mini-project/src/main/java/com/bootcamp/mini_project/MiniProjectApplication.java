package com.bootcamp.mini_project;

import com.vaadin.flow.component.page.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;

@Push
@SpringBootApplication
public class MiniProjectApplication implements AppShellConfigurator {
    static void main(String[] args) {
        SpringApplication.run(MiniProjectApplication.class, args);
    }
}