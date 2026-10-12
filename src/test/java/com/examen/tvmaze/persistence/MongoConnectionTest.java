package com.examen.tvmaze.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
class MongoConnectionTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void pingsTheConfiguredDatabase() {
        Document result = mongoTemplate.executeCommand("{ ping: 1 }");

        assertThat(result.get("ok")).isEqualTo(1.0);
    }
}
