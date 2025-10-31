package com.healthatlas.auth.config;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

@ApplicationScoped
public class JdbiConfig {

    @Produces
    @ApplicationScoped
    public Jdbi jdbi(AgroalDataSource dataSource) {
        return Jdbi.create(dataSource)
                .installPlugin(new SqlObjectPlugin());
    }
}
