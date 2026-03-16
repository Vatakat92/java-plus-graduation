package ru.practicum.integration.event.dto;

import java.util.List;

public record EventIdBatchRequest(List<Long> ids) {
}
