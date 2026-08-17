package com.bluesky.simulations;

import com.bluesky.simulations.config.AsyncSyncConfiguration;
import com.bluesky.simulations.config.EmbeddedSQL;
import com.bluesky.simulations.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        BlueSkyApp.class,
        JacksonConfiguration.class,
        AsyncSyncConfiguration.class,
        com.bluesky.simulations.config.JacksonHibernateConfiguration.class,
    }
)
@EmbeddedSQL
public @interface IntegrationTest {}
