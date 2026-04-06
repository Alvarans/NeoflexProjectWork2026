package com.example.database;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    @Bean
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
        liquibase.setShouldRun(true); // включаем выполнение миграций
        return liquibase;
    }

    @Bean
    public CommandLineRunner liquibaseTest(SpringLiquibase liquibase) {
        return args -> System.out.println("Liquibase bean: " + liquibase);
    }
}
