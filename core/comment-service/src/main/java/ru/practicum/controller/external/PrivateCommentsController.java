package ru.practicum.controller.external;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.external.CommentShortDto;
import ru.practicum.dto.external.NewCommentDto;
import ru.practicum.dto.external.UpdateCommentDto;
import ru.practicum.service.CommentService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class PrivateCommentsController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(CREATED)
    public CommentShortDto createComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @RequestBody @Valid NewCommentDto dto) {
        return commentService.createComment(dto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(OK)
    public CommentShortDto patchComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @PathVariable @Positive Long commentId,
            @RequestBody @Valid UpdateCommentDto dto) {
        return commentService.patchComment(dto, userId, eventId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteComment(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long eventId,
            @PathVariable @Positive Long commentId
    ) {
        commentService.deleteCommentByPrivate(userId, eventId, commentId);
    }
}
