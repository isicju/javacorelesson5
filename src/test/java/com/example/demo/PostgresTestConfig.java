package com.example.demo;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;

import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;
import org.springframework.core.io.Resource;

@TestConfiguration
public class PostgresTestConfig {

    @Value("classpath:schema.sql")
    private Resource schemaResource;

    @Value("${postgres.password}")
    private String password;

    @Bean
    public DataSource postgresDataSource(DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration) throws Exception {
        String schema = StreamUtils.copyToString(schemaResource.getInputStream(), StandardCharsets.UTF_8);
        Path tempDir = Files.createTempDirectory("embedded-postgres");

        EmbeddedPostgres pg = EmbeddedPostgres.builder()
                .setDataDirectory(tempDir.toFile())
                .setPort(0)
                .start();

        DataSource ds = pg.getPostgresDatabase();

        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute(schema);
        }catch (Exception e){
            System.out.println("Error creating embedded database " + e.getMessage());
        }

        return ds;
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
