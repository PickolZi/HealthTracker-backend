package dev.jamesdsan.backend.repository;

import dev.jamesdsan.backend.entity.MuscleGroup;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MuscleGroupRepository extends JpaRepository<MuscleGroup, Long> {
}
