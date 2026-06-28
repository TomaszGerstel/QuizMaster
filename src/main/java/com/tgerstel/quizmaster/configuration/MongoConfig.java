package com.tgerstel.quizmaster.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@ConditionalOnProperty(name = "mongo.enabled", havingValue = "true", matchIfMissing = true)
@EnableMongoAuditing
public class MongoConfig {
}
