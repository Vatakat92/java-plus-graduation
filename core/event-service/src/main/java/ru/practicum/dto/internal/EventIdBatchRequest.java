package ru.practicum.dto.internal;

import java.util.List;

public record EventIdBatchRequest(List<Long> ids) {
}
