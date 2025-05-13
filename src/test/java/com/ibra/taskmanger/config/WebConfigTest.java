package com.ibra.taskmanger.config;

import com.ibra.taskmanager.config.WebConfig;
import com.ibra.taskmanager.config.AppConfig; // Import AppConfig
import com.ibra.taskmanager.controller.TaskController;
import com.ibra.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(classes = {WebConfig.class, AppConfig.class}) // Load both configurations
@WebAppConfiguration
public class WebConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void testWebApplicationContextLoads() {
        assertNotNull(context);
    }

    @Test
    void testWebMvcConfigurerBeanCreation() {
        WebMvcConfigurer webMvcConfigurer = context.getBean(WebMvcConfigurer.class);
        assertNotNull(webMvcConfigurer);
    }

    @Test
    void testThymeleafViewResolverBeanCreation() {
        ThymeleafViewResolver viewResolver = context.getBean(ThymeleafViewResolver.class);
        assertNotNull(viewResolver);
        assertTrue(viewResolver.isCache());
    }

    @Test
    void testSpringTemplateEngineBeanCreation() {
        SpringTemplateEngine templateEngine = context.getBean(SpringTemplateEngine.class);
        assertNotNull(templateEngine);
    }

    @Test
    void testTaskControllerBeanCreation() {
        TaskController taskController = context.getBean(TaskController.class);
        assertNotNull(taskController);
    }

    @Test
    void testTaskServiceBeanCreation() {
        TaskService taskService = context.getBean(TaskService.class);
        assertNotNull(taskService);
    }
}
