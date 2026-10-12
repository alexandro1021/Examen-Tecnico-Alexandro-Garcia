package com.examen.tvmaze.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowCacheRepository extends MongoRepository<CachedShow, Integer> {
}
