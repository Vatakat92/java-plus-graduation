package ru.practicum.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "compilations", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "compilation_events", joinColumns = @JoinColumn(name = "compilation_id"))
    @Column(name = "event_id")
    @Builder.Default
    private List<Long> eventIds = new ArrayList<>();

    @Column(nullable = false)
    private boolean pinned;

    @Column(nullable = false, length = 50, unique = true)
    private String title;
}