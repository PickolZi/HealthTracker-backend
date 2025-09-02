package dev.jamesdsan.backend.repository;

import dev.jamesdsan.backend.entity.User;
import dev.jamesdsan.backend.entity.WorkoutEntry;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutEntryRepository extends JpaRepository<WorkoutEntry, Long> {
    public List<WorkoutEntry> findAllByUser(User user);

    public List<WorkoutEntry> findAllByUserAndDate(User user, LocalDate date, Pageable pageable);
}
