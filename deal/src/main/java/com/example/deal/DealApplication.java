package com.example.deal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class DealApplication {

    public static void main(String[] args) {
        SpringApplication.run(DealApplication.class, args);
    }

    @Bean
    CommandLineRunner checkLiquibaseConfig(Environment env) {
        return args -> {
            System.out.println("LIQUIBASE ENABLED = " +
                    env.getProperty("spring.liquibase.enabled"));
            System.out.println("LIQUIBASE CHANGELOG = " +
                    env.getProperty("spring.liquibase.change-log"));
        };
    }
}
