package ru.practicum.ewm.service.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.service.user.dto.NewUserRequest;
import ru.practicum.ewm.service.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto add(NewUserRequest req);

    List<UserDto> listByIds(java.util.List<Long> ids, Pageable pageable);

    void delete(long userId);

}
