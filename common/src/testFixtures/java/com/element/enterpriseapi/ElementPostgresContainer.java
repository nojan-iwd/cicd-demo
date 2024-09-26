package com.element.enterpriseapi;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;

public class ElementPostgresContainer extends PostgreSQLContainer<ElementPostgresContainer> {
    private static final String IMAGE_NAME = "postgres:alpine3.20";
    private static ElementPostgresContainer container;
    private String[] initScriptPaths;

    private ElementPostgresContainer() {
        super(IMAGE_NAME);
    }

    public static ElementPostgresContainer getInstance() {
        if (container == null) {
            container = new ElementPostgresContainer()
                    .withDatabaseName("postgres")
                    .withUsername("postgres")
                    .withPassword("password1")
                    .withInitScripts("seed_edb_postgres.sql", "seed_mf_postgres.sql");
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_CONTAINER_DRIVER", container.getDriverClassName());
        System.setProperty("TEST_CONTAINER_URL", container.getJdbcUrl());
        System.setProperty("TEST_CONTAINER_USERNAME", container.getUsername());
        System.setProperty("TEST_CONTAINER_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        // NO-OP (intentionally as we don't want container to stop between tests)
    }

    @Override
    protected void runInitScriptIfRequired() {
        if (initScriptPaths != null) {
            for (String initScriptPath : initScriptPaths) {
                ScriptUtils.runInitScript(getDatabaseDelegate(), initScriptPath);
            }
        }
    }



    public ElementPostgresContainer withInitScripts(String... scriptPaths) {
        this.initScriptPaths = scriptPaths;
        return this;
    }
}
