package ru.practicum.integration.stats.dto;

import lombok.Data;

@Data
public class ViewStatsDto {
    private String app;
    private String uri;
    private long hits;
}
