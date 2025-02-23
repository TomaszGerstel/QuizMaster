package com.tgerstel.quizmaster.configuration;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Setter
@Component
@ConfigurationProperties(prefix = "spring.liquibase")
public class DatabaseChangelogUpdater {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseChangelogUpdater.class);

    private String url;

    private String changeLog;

    private boolean enabled;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        if (enabled) {
            try {
                runLiquibaseUpdate();
            } catch (LiquibaseException e) {
                LOGGER.error("Error while updating the database with Liquibase", e);
            }
        } else {
            LOGGER.info("Liquibase update is disabled. Set liquibase.enabled=true to enable it.");
        }
    }

    private void runLiquibaseUpdate() throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().openDatabase(url, null, null, null, null);
        Liquibase liquibase = new Liquibase(changeLog, new ClassLoaderResourceAccessor(), database);
        liquibase.update("");
        LOGGER.info("Database updated successfully using Liquibase");
    }

}