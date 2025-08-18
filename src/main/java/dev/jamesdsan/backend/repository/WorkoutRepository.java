package dev.jamesdsan.backend.repository;

import dev.jamesdsan.backend.entity.Workout;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
}
