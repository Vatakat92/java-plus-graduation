package ru.practicum.handler;

import ru.practicum.kafka.avro.EventSimilarityAvro;

public interface SimilarityHandler {
    void handle(EventSimilarityAvro eventSimilarityAvro);
}
