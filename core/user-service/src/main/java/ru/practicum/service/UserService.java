package ru.practicum.service;

import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.dto.external.NewUserRequest;
import ru.practicum.dto.external.UserDto;
import ru.practicum.dto.internal.UserShortDto;
import ru.practicum.exceptions.EmailAlreadyExistsException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // External API
    @Transactional
    public UserDto add(NewUserRequest newUserRequest) {
        String email = newUserRequest.getEmail();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(
                    "User with email '" + email + "' already exists."
            );
        }

        try {
            User saved = userRepository.save(User.builder()
                    .name(newUserRequest.getName())
                    .email(email)
                    .build()
            );
            return UserMapper.toDto(saved);

        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException(
                    "User with email '" + email + "' already exists."
            );
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> listByIds(List<Long> ids, Pageable pageable) {

        Pageable p = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(
                (int) (pageable.getOffset() / pageable.getPageSize()),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "id"));

        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(p).getContent().stream()
                    .map(UserMapper::toDto)
                    .toList();
        } else {
            return userRepository.findByIdIn(ids, p).getContent().stream()
                    .map(UserMapper::toDto)
                    .toList();
        }
    }

    @Transactional
    public void delete(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(
                    "User with id=" + userId + " was not found."
            );
        }
        userRepository.deleteById(userId);
    }

    // Internal API
    @Transactional(readOnly = true)
    public UserShortDto getUserById(Long userId) {
        return UserMapper.toShortDto(userRepository.findById(userId)
                .orElseThrow(
                        () -> new NotFoundException(
                                "User with id=" + userId + " was not found."
                        )
                )
        );
    }

    @Transactional(readOnly = true)
    public Map<Long, UserShortDto> getUsersByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        User::getId,
                        UserMapper::toShortDto
                ));
    }
}
