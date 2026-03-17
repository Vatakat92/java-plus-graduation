package ru.practicum.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Optional<Compilation> findByTitleIgnoreCase(String title);

    Page<Compilation> findByPinned(Boolean pinned, Pageable pageable);
}