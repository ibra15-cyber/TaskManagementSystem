package com.ibra.taskmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/ping")
    @ResponseBody
    public String ping() {
        return "Spring MVC is working! The controller mapping is working correctly.";
    }

    @GetMapping("/view-test")
    public String viewTest(Model model) {
        model.addAttribute("message", "If you can see this message, Thymeleaf view resolution is working!");
        return "debug";
    }
}