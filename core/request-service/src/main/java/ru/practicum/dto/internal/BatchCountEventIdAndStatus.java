package ru.practicum.dto.internal;

import java.util.List;
import ru.practicum.model.RequestStatus;

public record BatchCountEventIdAndStatus(List<Long> eventIds, RequestStatus status) {
}