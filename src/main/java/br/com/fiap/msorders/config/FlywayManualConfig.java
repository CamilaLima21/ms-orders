package br.com.fiap.msorders.config;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class FlywayManualConfig {

    private final DataSource dataSource;

    public FlywayManualConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void migrate() {
        Flyway.configure()
              .dataSource(dataSource)
              .locations("classpath:db/migration")
              .baselineOnMigrate(true)
              .load()
              .migrate();
    }
}
