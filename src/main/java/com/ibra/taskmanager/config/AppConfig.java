package com.ibra.taskmanager.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@PropertySource("classpath:database.properties")
@EnableTransactionManagement
@ComponentScan(basePackages = "com.ibra.taskmanager")
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    private final Environment environment;

    @Autowired
    public AppConfig(Environment environment) {
        if (environment == null) {
            logger.error("Environment cannot be null.");
            throw new IllegalArgumentException("Environment cannot be null");
        }
        this.environment = environment;
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        try {
            String driverClassName = environment.getRequiredProperty("jdbc.driverClassName");
            String url = environment.getRequiredProperty("jdbc.url");
            String username = environment.getRequiredProperty("jdbc.username");
            String password = environment.getRequiredProperty("jdbc.password");

            if (isNullOrEmpty(driverClassName) || isNullOrEmpty(url) || isNullOrEmpty(username) || isNullOrEmpty(password)) {
                logger.error("Database properties cannot be null or empty.");
                throw new IllegalArgumentException("Database properties cannot be null or empty.");
            }

            dataSource.setDriverClassName(driverClassName);
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setInitialSize(5);
            dataSource.setMaxTotal(20);
            logger.info("DataSource bean initialized successfully.");
            return dataSource;

        } catch (IllegalStateException e) {
            logger.error("Failed to retrieve required database property.", e);
            throw new IllegalStateException("Failed to retrieve required database property: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid database configuration.", e);
            throw new IllegalArgumentException("Invalid database configuration: " + e.getMessage());
        }
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        try {
            sessionFactory.setDataSource(dataSource());
            sessionFactory.setPackagesToScan("com.ibra.taskmanager.entity");
            sessionFactory.setHibernateProperties(hibernateProperties());
            sessionFactory.afterPropertiesSet(); // Initialize the SessionFactory
            logger.info("SessionFactory bean initialized successfully.");
            return sessionFactory;
        } catch (Exception e) {
            logger.error("Failed to create SessionFactory.", e);
            throw new RuntimeException("Failed to create SessionFactory: " + e.getMessage(), e);
        }
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        try {
            String dialect = environment.getRequiredProperty("hibernate.dialect");
            String showSql = environment.getRequiredProperty("hibernate.show_sql");
            String formatSql = environment.getRequiredProperty("hibernate.format_sql");
            String hbm2ddlAuto = environment.getRequiredProperty("hibernate.hbm2ddl.auto");

            if (isNullOrEmpty(dialect) || isNullOrEmpty(showSql) || isNullOrEmpty(formatSql) || isNullOrEmpty(hbm2ddlAuto)) {
                logger.error("Hibernate properties cannot be null or empty.");
                throw new IllegalArgumentException("Hibernate properties cannot be null or empty.");
            }

            properties.put("hibernate.dialect", dialect);
            properties.put("hibernate.show_sql", showSql);
            properties.put("hibernate.format_sql", formatSql);
            properties.put("hibernate.hbm2ddl.auto", hbm2ddlAuto);
            logger.info("Hibernate properties loaded.");
            return properties;

        } catch (IllegalStateException e) {
            logger.error("Failed to retrieve required hibernate property.", e);
            throw new IllegalStateException("Failed to retrieve required hibernate property: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid hibernate configuration.", e);
            throw new IllegalArgumentException("Invalid hibernate configuration: " + e.getMessage());
        }
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        if (sessionFactory == null) {
            logger.error("SessionFactory cannot be null.");
            throw new IllegalArgumentException("SessionFactory cannot be null");
        }
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);
        logger.info("TransactionManager bean initialized successfully.");
        return txManager;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}