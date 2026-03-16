package ru.practicum.mapper;

import ru.practicum.dto.external.CommentFullDto;
import ru.practicum.dto.external.CommentShortDto;
import ru.practicum.model.Comment;

public final class CommentMapper {

    private CommentMapper() {}
    public static CommentShortDto toShortDto(Comment comment) {
        if (comment == null)
            return null;

        return CommentShortDto.builder()
                .text(comment.getText())
                .commentatorId(comment.getCommentatorId())
                .publishedOn(comment.getPublishedOn())
                .build();
    }

    public static CommentFullDto toFullDto(Comment comment) {
        if (comment == null)
            return null;

        return CommentFullDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .commentatorId(comment.getCommentatorId())
                .eventId(comment.getEventId())
                .publishedOn(comment.getPublishedOn())
                .deleted(comment.isDeleted())
                .build();
    }
}