package ru.practicum.integration.request.dto;

import java.util.Set;

public record BatchCountEventIdandStatus(Set<Long> eventIds, RequestStatus status) {
}