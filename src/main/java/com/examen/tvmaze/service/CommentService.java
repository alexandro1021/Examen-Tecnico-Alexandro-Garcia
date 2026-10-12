package com.examen.tvmaze.service;

import com.examen.tvmaze.dto.CommentView;
import com.examen.tvmaze.dto.CreateCommentRequest;
import com.examen.tvmaze.dto.RequestStatusResponse;
import com.examen.tvmaze.persistence.ShowComment;
import com.examen.tvmaze.persistence.ShowCommentRepository;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final ShowCommentRepository showCommentRepository;
    private final ShowService showService;
    private final Clock clock;

    public CommentService(ShowCommentRepository showCommentRepository, ShowService showService, Clock clock) {
        this.showCommentRepository = showCommentRepository;
        this.showService = showService;
        this.clock = clock;
    }

    public RequestStatusResponse create(CreateCommentRequest request) {
        showService.getShow(request.showId());
        showCommentRepository.save(new ShowComment(
                request.showId(),
                request.comment().trim(),
                request.rating(),
                clock.instant()));
        return new RequestStatusResponse("CREATED");
    }

    public List<CommentView> findByShowId(int showId) {
        return showCommentRepository.findByShowIdOrderByCreatedAtAsc(showId).stream()
                .map(CommentService::toView)
                .toList();
    }

    public Map<Integer, List<CommentView>> findByShowIds(Collection<Integer> showIds) {
        if (showIds == null || showIds.isEmpty()) {
            return Map.of();
        }
        Map<Integer, List<CommentView>> grouped = new LinkedHashMap<>();
        for (ShowComment comment : showCommentRepository.findByShowIdInOrderByCreatedAtAsc(showIds)) {
            grouped.computeIfAbsent(comment.getShowId(), ignored -> new ArrayList<>())
                    .add(toView(comment));
        }
        return grouped;
    }

    private static CommentView toView(ShowComment comment) {
        return new CommentView(comment.getComment(), comment.getRating());
    }
}
