package ru.practicum.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.external.NewCompilationDto;
import ru.practicum.dto.external.UpdateCompilationDto;
import ru.practicum.model.Compilation;
import ru.practicum.repository.CompilationRepository;

@Service
@RequiredArgsConstructor
public class CompilationWriteService {

    private final CompilationRepository compilationRepository;

    @Transactional
    public Compilation createCompilation(NewCompilationDto dto) {

        Compilation compilation = Compilation.builder()
                .title(dto.getTitle())
                .pinned(dto.getPinned())
                .eventIds(dto.getEvents() == null ? List.of() : dto.getEvents())
                .build();

        return compilationRepository.save(compilation);
    }

    @Transactional
    public void deleteCompilation(Compilation compilation) {
        compilationRepository.deleteById(compilation.getId());
    }

    @Transactional
    public Compilation patchCompilation(Compilation compilation, UpdateCompilationDto dto, String title) {

        compilation.setTitle(title);

        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null) {
            compilation.setEventIds(dto.getEvents());
        }
        return compilationRepository.save(compilation);
    }
}