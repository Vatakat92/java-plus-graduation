package ru.practicum.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private LocalDateTime created;

    private Long eventId;

    private Long requesterId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}