package com.healthatlas.auth;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

public class PostgresTestResource implements QuarkusTestResourceLifecycleManager {

    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:15"))
                    .withDatabaseName("cerberus_test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @Override
    public Map<String, String> start() {
        POSTGRES.start();
        return Map.of(
        "quarkus.datasource.jdbc.url", POSTGRES.getJdbcUrl(),
        "quarkus.datasource.username", POSTGRES.getUsername(),
        "quarkus.datasource.password", POSTGRES.getPassword()
        );
    }

    @Override
    public void stop() {
        POSTGRES.stop();
    }
}
