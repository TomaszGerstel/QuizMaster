package com.tgerstel.quizmaster.configuration;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Component
@ConfigurationProperties(prefix = "spring.liquibase")
public class DatabaseChangelogUpdater {

    private String url;

    private String changeLog;

    private boolean enabled;

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        if (enabled) {
            try {
                runLiquibaseUpdate();
            } catch (LiquibaseException e) {
                log.error("Error while updating the database with Liquibase", e);
            }
        } else {
            log.info("Liquibase update is disabled. Set liquibase.enabled=true to enable it.");
        }
    }

    private void runLiquibaseUpdate() throws LiquibaseException {
        Database database = DatabaseFactory.getInstance().openDatabase(url, null, null, null, null);
        Liquibase liquibase = new Liquibase(changeLog, new ClassLoaderResourceAccessor(), database);
        liquibase.update("");
        log.info("Database updated successfully using Liquibase");
    }

}