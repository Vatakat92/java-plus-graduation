package ru.practicum.service;

import static ru.practicum.integration.event.dto.EventState.PUBLISHED;
import static ru.practicum.integration.request.dto.RequestStatus.CONFIRMED;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.external.CommentFullDto;
import ru.practicum.dto.external.CommentShortDto;
import ru.practicum.dto.external.NewCommentDto;
import ru.practicum.dto.external.UpdateCommentDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.integration.event.EventServiceGateway;
import ru.practicum.integration.event.dto.EventSummaryDto;
import ru.practicum.integration.request.RequestServiceGateway;
import ru.practicum.integration.request.dto.RequestShortDto;
import ru.practicum.integration.user.UserServiceGateway;
import ru.practicum.integration.user.dto.UserShortDto;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;

@Service
@AllArgsConstructor
public class CommentService {

    private final UserServiceGateway userServiceGateway;
    private final EventServiceGateway eventServiceGateway;
    private final RequestServiceGateway requestServiceGateway;
    private final CommentWriteService commentWriteService;
    private final CommentRepository commentRepository;

    public CommentShortDto createComment(NewCommentDto dto, Long userId, Long eventId) {
        UserShortDto commentator = userServiceGateway.getUserById(userId);
        EventSummaryDto event = eventServiceGateway.getEventById(eventId);
        RequestShortDto request = requestServiceGateway.getByRequesterById(userId, eventId);

        if (request.getStatus() != CONFIRMED) {
            throw new IllegalArgumentException(
                    "Unable to create comment. Request status must be CONFIRMED. Current value:" + request.getStatus()
            );
        }

        if (event.getState() != PUBLISHED) {
            throw new IllegalArgumentException(
                    "Unable to create comment. Event id=" + eventId + "not published"
            );
        }

        return commentWriteService.createComment(dto, commentator.getId(), event.getId());
    }

    public CommentShortDto patchComment(UpdateCommentDto dto,
                                        Long userId,
                                        Long eventId,
                                        Long commentId) {

        EventSummaryDto event = eventServiceGateway.getEventById(eventId);

        if (event.getState() != PUBLISHED) {
            throw new IllegalArgumentException(
                    "Unable to patch comment. Event id=" + eventId + "not published"
            );
        }

        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException(
                        "Unable to patch comment. Comment id=" + commentId + "not found"
                )
        );

        if (!Objects.equals(comment.getEventId(), eventId)) {
            throw new IllegalArgumentException(
                    "Comment does not belong to event with id=" + eventId
            );
        }

        if (!Objects.equals(comment.getCommentatorId(), userId)) {
            throw new IllegalArgumentException(
                    "Unable to patch comment. User with id=" + userId + " is not creator of comment id=" + commentId
            );
        }

        String text = dto.getText() == null || dto.getText().isBlank() ? comment.getText() : dto.getText();

        return commentWriteService.updateCommentText(comment, text);
    }

    public void deleteCommentByPrivate(Long userId, Long eventId, Long commentId) {

        EventSummaryDto event = eventServiceGateway.getEventById(eventId);

        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException(
                        "Unable to delete comment. Comment id=" + commentId + "not found"
                )
        );

        if (!Objects.equals(comment.getCommentatorId(), userId)) {
            throw new IllegalArgumentException(
                    "Unable to delete comment. User with id=" + userId + " is not creator of comment id=" + commentId
            );
        }

        if (event.getState() != PUBLISHED) {
            throw new IllegalArgumentException(
                    "Unable to delete comment. Event id=" + eventId + "not published"
            );
        }

        if (!Objects.equals(comment.getEventId(), eventId)) {
            throw new IllegalArgumentException(
                    "Comment does not belong to event with id=" + eventId
            );
        }

        commentWriteService.commentDelete(comment);
    }

    public void deleteCommentByAdmin(Long eventId, Long commentId) {

        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException(
                        "Unable to delete comment. Comment id=" + commentId + "not found"
                )
        );

        if (!Objects.equals(comment.getEventId(), eventId)) {
            throw new IllegalArgumentException(
                    "Comment does not belong to event with id=" + eventId
            );
        }

        commentWriteService.commentDelete(comment);
    }

    @Transactional(readOnly = true)
    public CommentShortDto getComment(Long eventId, Long userId, Long commentId) {
        Comment comment = commentRepository.findByIdAndDeleted(commentId, false).orElseThrow(
                () -> new NotFoundException(
                        "Unable to get comment. Comment id=" + commentId + " not found"
                )
        );

        if (!Objects.equals(comment.getEventId(), eventId)) {
            throw new IllegalArgumentException(
                    "Comment does not belong to event with id=" + eventId
            );
        }

        if (!Objects.equals(comment.getCommentatorId(), userId)) {
            throw new IllegalArgumentException(
                    "Unable to patch comment. User with id=" + userId + " is not creator of comment id=" + commentId
            );
        }

        return CommentMapper.toShortDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentShortDto> getAllCommentsByEventPublic(Long eventId) {

        List<Comment> commentList = commentRepository.findByEventIdAndDeleted(eventId, false);

        if (commentList.isEmpty()) {
            return Collections.emptyList();
        }

        return commentList.stream()
                .map(CommentMapper::toShortDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentFullDto> getAllCommentsByEventAdmin(Long eventId) {

        List<Comment> commentList = commentRepository.findByEventId(eventId);
        if (commentList.isEmpty()) {
            return Collections.emptyList();
        }

        return commentList.stream()
                .map(CommentMapper::toFullDto)
                .toList();
    }
}
