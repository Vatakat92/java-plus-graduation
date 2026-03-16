package ru.practicum.mapper;

import ru.practicum.dto.external.UserDto;
import ru.practicum.dto.internal.UserShortDto;
import ru.practicum.model.User;

public final class UserMapper {

    private UserMapper() {}

    public static UserDto toDto(User u){
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .build();
    }

    public static UserShortDto toShortDto(User u){
        return UserShortDto.builder()
                .id(u.getId())
                .name(u.getName())
                .build();
    }
}
