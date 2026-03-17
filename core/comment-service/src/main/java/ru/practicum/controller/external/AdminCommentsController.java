package ru.practicum.controller.external;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.List;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.external.CommentFullDto;
import ru.practicum.service.CommentService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events/{eventId}/comments")
public class AdminCommentsController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentFullDto> getAllCommentsByEvent(
            @PathVariable @Positive Long eventId
    ) {
        return commentService.getAllCommentsByEventAdmin(eventId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteComment(
            @PathVariable @Positive Long eventId,
            @PathVariable @Positive Long commentId) {
        commentService.deleteCommentByAdmin(eventId, commentId);
    }
}
