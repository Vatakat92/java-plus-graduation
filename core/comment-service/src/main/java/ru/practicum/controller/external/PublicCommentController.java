package ru.practicum.controller.external;

import java.util.List;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.external.CommentShortDto;
import ru.practicum.service.CommentService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentShortDto> getAllCommentsByEvent(
            @PathVariable @Positive Long eventId
    ) {
        return commentService.getAllCommentsByEventPublic(eventId);
    }

    @GetMapping("/{commentId}/user/{userId}")
    public CommentShortDto getCommentsOfUser(
            @PathVariable @Positive Long eventId,
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long commentId
    ) {
        return commentService.getComment(eventId, userId, commentId);
    }
}
