package dev.jamesdsan.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.entity.MuscleGroup;
import dev.jamesdsan.backend.entity.Workout;
import dev.jamesdsan.backend.repository.MuscleGroupRepository;
import dev.jamesdsan.backend.repository.WorkoutRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutMuscleGroupService {
    @Autowired
    private final WorkoutRepository workoutRepository;

    @Autowired
    private final MuscleGroupRepository muscleGroupRepository;

    public void createLinkWorkoutToMuscleGroup(long workoutId, long muscleGroupId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new ValidationException("Workout not found when linking to muscle group."));
        MuscleGroup muscleGroup = muscleGroupRepository.findById(muscleGroupId)
                .orElseThrow(() -> new ValidationException("MuscleGroup not found when linking to workout."));

        workout.getMuscleGroups().add(muscleGroup);
        muscleGroup.getWorkouts().add(workout);
        workoutRepository.save(workout);
    }

    public void deleteLinkWorkoutToMuscleGroup(long workoutId, long muscleGroupId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new ValidationException("Workout not found when linking to muscle group."));
        MuscleGroup muscleGroup = muscleGroupRepository.findById(muscleGroupId)
                .orElseThrow(() -> new ValidationException("MuscleGroup not found when linking to workout."));

        if (!workout.getMuscleGroups().contains(muscleGroup) || !muscleGroup.getWorkouts().contains(workout)) {
            throw new ValidationException("Error when trying to delete workout to muscle group link.");
        }

        workout.getMuscleGroups().remove(muscleGroup);
        muscleGroup.getWorkouts().remove(workout);
        workoutRepository.save(workout);
    }

}
