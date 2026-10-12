package com.examen.tvmaze.service;

import com.examen.tvmaze.dto.CommentView;
import java.util.List;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

@Service
public class ShowService {

    private final ShowCatalog showCatalog;
    private final CommentService commentService;

    public ShowService(ShowCatalog showCatalog, CommentService commentService) {
        this.showCatalog = showCatalog;
        this.commentService = commentService;
    }

    public JsonNode getShow(int showId) {
        return withComments(showCatalog.load(showId), commentService.findByShowId(showId));
    }

    private JsonNode withComments(JsonNode show, List<CommentView> comments) {
        ObjectNode node = (ObjectNode) show.deepCopy();
        ArrayNode array = node.putArray("comments");
        for (CommentView comment : comments) {
            ObjectNode item = array.addObject();
            item.put("comment", comment.comment());
            item.put("rating", comment.rating());
        }
        return node;
    }
}
