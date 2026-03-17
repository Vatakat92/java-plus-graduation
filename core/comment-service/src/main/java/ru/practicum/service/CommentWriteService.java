package ru.practicum.service;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.external.CommentShortDto;
import ru.practicum.dto.external.NewCommentDto;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentWriteService {

    private final CommentRepository commentRepository;

    @Transactional
    public CommentShortDto createComment(NewCommentDto dto, Long userId, Long eventId) {
        Comment comment = Comment.builder()
                .text(dto.getText())
                .commentatorId(userId)
                .eventId(eventId)
                .publishedOn(LocalDateTime.now())
                .deleted(false)
                .build();

        Comment saved = commentRepository.save(comment);
        return CommentMapper.toShortDto(saved);
    }

    @Transactional
    public CommentShortDto updateCommentText(Comment comment, String text) {
        comment.setText(text);
        return CommentMapper.toShortDto(commentRepository.save(comment));
    }


    @Transactional
    public void commentDelete(Comment comment) {
        comment.setDeleted(true);
    }
 }
