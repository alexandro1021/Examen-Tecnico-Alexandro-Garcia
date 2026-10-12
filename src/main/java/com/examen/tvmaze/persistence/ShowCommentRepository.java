package com.examen.tvmaze.persistence;

import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShowCommentRepository extends MongoRepository<ShowComment, String> {

    List<ShowComment> findByShowIdOrderByCreatedAtAsc(Integer showId);

    List<ShowComment> findByShowIdInOrderByCreatedAtAsc(Collection<Integer> showIds);
}
