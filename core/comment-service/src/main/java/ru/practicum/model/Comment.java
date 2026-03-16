package ru.practicum.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "user_id", nullable = false)
    private Long commentatorId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "published_on", nullable = false)
    private LocalDateTime publishedOn;

    @Column(name = "deleted")
    private boolean deleted;
}
