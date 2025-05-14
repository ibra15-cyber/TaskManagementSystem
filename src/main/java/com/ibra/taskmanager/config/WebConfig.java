package com.ibra.taskmanager.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.ibra.taskmanager.controller"})
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    private final ApplicationContext applicationContext;

    public WebConfig(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            logger.error("ApplicationContext cannot be null.");
            throw new IllegalArgumentException("ApplicationContext cannot be null.");
        }
        this.applicationContext = applicationContext;
    }

    // Thymeleaf Template Resolver
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        try {
            templateResolver.setApplicationContext(applicationContext);
            templateResolver.setPrefix("/WEB-INF/views/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode("HTML");
            templateResolver.setCacheable(false);
            logger.info("Template resolver initialized.");
            return templateResolver;
        } catch (IllegalArgumentException e) {
            logger.error("Error configuring template resolver.", e);
            throw new IllegalArgumentException("Error configuring template resolver: " + e.getMessage());
        }
    }

    // Thymeleaf Template Engine
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        try {
            templateEngine.setTemplateResolver(templateResolver());
            templateEngine.setEnableSpringELCompiler(true);
            logger.info("Template engine initialized.");
            return templateEngine;
        } catch (IllegalArgumentException e) {
            logger.error("Error configuring template engine.", e);
            throw new IllegalArgumentException("Error configuring template engine: " + e.getMessage());
        }
    }

    // View Resolver Configuration
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        try {
            ThymeleafViewResolver resolver = new ThymeleafViewResolver();
            resolver.setTemplateEngine(templateEngine());
            resolver.setCharacterEncoding("UTF-8");
            registry.viewResolver(resolver);
            logger.info("View resolvers configured.");
        } catch (IllegalArgumentException e) {
            logger.error("Error configuring view resolvers.", e);
            throw new IllegalArgumentException("Error configuring view resolvers: " + e.getMessage());
        }
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        try {
            viewResolver.setTemplateEngine(templateEngine());
<<<<<<< HEAD
            viewResolver.setOrder(1);
            viewResolver.setViewNames(new String[]{"*.html", "*.xhtml"});
=======
            viewResolver.setOrder(1); // If you have multiple view resolvers
            viewResolver.setViewNames(new String[]{"*.html", "*.xhtml"}); // Specify which view names this resolver handles
>>>>>>> 063f6551076b86830acf172a465b709021f783cb
            logger.info("View resolver bean initialized.");
            return viewResolver;
        } catch (IllegalArgumentException e) {
            logger.error("Error configuring the view resolver bean.", e);
            throw new IllegalArgumentException("Error configuring the view resolver bean: " + e.getMessage());
        }
    }

    // Static Resources Configuration
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        try {
            WebMvcConfigurer.super.addResourceHandlers(registry);
            registry.addResourceHandler("/static/**").addResourceLocations("/static/");
            registry.addResourceHandler("/css/**").addResourceLocations("/static/css/");
            registry.addResourceHandler("/js/**").addResourceLocations("/static/js/");
            registry.addResourceHandler("/images/**").addResourceLocations("/static/images/");
            logger.info("Resource handlers configured.");
        } catch (IllegalArgumentException e) {
            logger.error("Error configuring resource handlers.", e);
            throw new IllegalArgumentException("Error configuring resource handlers: " + e.getMessage());
        }
    }

    // Message Source for i18n
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        try {
            messageSource.setBasename("messages");
            messageSource.setDefaultEncoding("UTF-8");
            logger.info("Message source configured.");
            return messageSource;
        } catch (IllegalArgumentException e) {
            logger.error("Error configuring message source.", e);
            throw new IllegalArgumentException("Error configuring message source: " + e.getMessage());
        }
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        try {
            configurer.enable(); // Allows DispatcherServlet to forward requests for static resources to the default servlet
            logger.info("Default servlet handling configured.");
        } catch (IllegalArgumentException e) {
            logger.error("Error configuring default servlet handling.", e);
            throw new IllegalArgumentException("Error configuring default servlet handling: " + e.getMessage());
        }
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        try {
            registry.addFormatter(new DateFormatter("yyyy-MM-dd'T'HH:mm"));
            logger.info("Date formatter added.");
        } catch (IllegalArgumentException e) {
            logger.error("Error adding formatter.", e);
            throw new IllegalArgumentException("Error adding formatter: " + e.getMessage());
        }
    }
}