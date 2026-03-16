package ru.practicum.integration.user.dto;

import java.util.List;

public record UserIdsBatchRequest(List<Long> ids) {
}
