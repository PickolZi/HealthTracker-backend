package dev.jamesdsan.backend.repository;

import dev.jamesdsan.backend.entity.User;
import dev.jamesdsan.backend.entity.WorkoutEntry;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutEntryRepository extends JpaRepository<WorkoutEntry, Long> {
    public List<WorkoutEntry> findAllByUser(User user);
}
