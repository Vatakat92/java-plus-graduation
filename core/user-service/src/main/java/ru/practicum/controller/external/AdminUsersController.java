package ru.practicum.controller.external;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Pageable;
import ru.practicum.dto.external.NewUserRequest;
import ru.practicum.dto.external.UserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.service.UserService;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated

public class AdminUsersController {

    private final UserService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public UserDto add(@Valid @RequestBody NewUserRequest newUserRequest) {
        return service.add(newUserRequest);
    }

    @GetMapping
    public List<UserDto> list(@RequestParam(value = "ids", required = false) List<Long> ids,
                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                              @RequestParam(defaultValue = "10") @Positive int size) {

        Pageable pageable = PageRequest.ofSize(size)
                .withPage(from / size)
                .withSort(Sort.Direction.ASC, "id");

        return service.listByIds(ids, pageable);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(NO_CONTENT)
    public void del(@PathVariable long userId) {
        service.delete(userId);
    }
}