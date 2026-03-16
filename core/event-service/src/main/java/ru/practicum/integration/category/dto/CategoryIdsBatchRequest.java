package ru.practicum.integration.category.dto;

import java.util.List;

public record CategoryIdsBatchRequest(List<Long> ids) {
}
