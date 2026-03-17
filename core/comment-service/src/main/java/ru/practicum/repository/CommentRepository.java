package ru.practicum.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndDeleted(Long commentId, boolean deleted);

    List<Comment> findByEventIdAndDeleted(Long commentId, boolean deleted);

    List<Comment> findByEventId(Long eventId);
}
