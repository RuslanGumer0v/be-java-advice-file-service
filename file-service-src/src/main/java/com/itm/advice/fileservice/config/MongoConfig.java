package com.itm.advice.fileservice.config;

import com.itm.advice.fileservice.domain.entity.BaseEntity;
import com.itm.advice.fileservice.util.ObjectIdToUUIDConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;

import java.util.List;
import java.util.UUID;

@Configuration
@EnableMongoAuditing
public class MongoConfig {
    @Bean
    public BeforeConvertCallback<BaseEntity> beforeSaveCallback() {
        return (entity, collection) -> {
            if (entity.getId() == null) {
                entity.setId(UUID.randomUUID());
            }
            return entity;
        };
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new ObjectIdToUUIDConverter()
        ));
    }
}
