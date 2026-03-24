package ru.practicum.service;

import ru.practicum.grpc.proto.UserActionProto;

public interface UserActionHandler {
    void handle(UserActionProto userActionProto);
}
