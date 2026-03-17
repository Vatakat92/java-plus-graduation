package ru.practicum.dto.internal;

import java.util.List;

public record UserIdsBatchRequest(List<Long> ids) {
}
