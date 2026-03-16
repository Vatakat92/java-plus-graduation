package ru.practicum.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.ParticipationRequest;
import ru.practicum.model.RequestStatus;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    Boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<ParticipationRequest> findAllByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);
}


