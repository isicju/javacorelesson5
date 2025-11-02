package com.example.demo;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;

import static io.micrometer.common.util.StringUtils.isBlank;

@Configuration
public class EmployeesRepositoryConfig {

    @Value("${postgres.password}")
    private String password;

    @Bean
    public DataSource postgresDataSource() {
        if(isBlank(password)) {
            throw new IllegalStateException("PostgreSQL password not set");
        }
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerNames(new String[]{"84.247.180.194"});
        dataSource.setPortNumbers(new int[]{5454});
        dataSource.setDatabaseName("mydb");
        dataSource.setUser("myuser");
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean
    public JdbcTemplate postgresJdbcTemplate(DataSource postgresDataSource) {
        return new JdbcTemplate(postgresDataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedJdbPostgresTemplate(DataSource postgresDataSource) {
        return new NamedParameterJdbcTemplate(postgresDataSource);
    }

}
