package ru.practicum.handler;

import ru.practicum.kafka.avro.UserActionAvro;

public interface UserActionHandler {

    void handle(UserActionAvro userActionAvro);
}
