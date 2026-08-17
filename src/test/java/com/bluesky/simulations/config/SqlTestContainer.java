package com.bluesky.simulations.config;

import org.testcontainers.containers.JdbcDatabaseContainer;

public interface SqlTestContainer {
    JdbcDatabaseContainer<?> getTestContainer();
}
