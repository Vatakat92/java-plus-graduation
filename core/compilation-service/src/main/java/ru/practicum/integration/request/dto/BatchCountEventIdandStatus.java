package ru.practicum.integration.request.dto;

import java.util.List;

public record BatchCountEventIdAndStatus(List<Long> eventIds, RequestStatus status) {
}