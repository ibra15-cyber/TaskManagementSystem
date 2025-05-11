package com.ibra.taskmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Welcome to Task Management System");
        return "home";
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        return "index";
    }
}