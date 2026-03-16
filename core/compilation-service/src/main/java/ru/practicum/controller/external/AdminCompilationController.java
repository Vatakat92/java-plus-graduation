package ru.practicum.controller.external;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.external.CompilationFullDto;
import ru.practicum.dto.external.NewCompilationDto;
import ru.practicum.dto.external.UpdateCompilationDto;
import ru.practicum.service.CompilationService;

@Validated
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(CREATED)
    public CompilationFullDto createCompilationByAdmin(@Valid @RequestBody NewCompilationDto dto) {
        return compilationService.createCompilationByAdmin(dto);
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteCompilationByAdmin(@PathVariable @Positive Long compilationId) {
        compilationService.deleteCompilationByAdmin(compilationId);
    }

    @PatchMapping("/{compilationId}")
    public CompilationFullDto patchCompilationByAdmin(
            @PathVariable @Positive Long compilationId,
            @Valid @RequestBody UpdateCompilationDto dto
    ) {
        return compilationService.patchCompilationByAdmin(compilationId, dto);
    }
}
