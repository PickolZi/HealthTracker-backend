package dev.jamesdsan.backend.service;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.dto.WorkoutResponse;
import dev.jamesdsan.backend.entity.Workout;
import dev.jamesdsan.backend.repository.WorkoutRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private final WorkoutRepository workoutRepository;

    public List<WorkoutResponse> listWorkouts() {
        return workoutRepository.findAll()
                .stream()
                .map(workout -> WorkoutResponse.builder()
                        .id(workout.getId())
                        .name(workout.getName())
                        .description(workout.getDescription())
                        .muscleGroups(workout.getMuscleGroups())
                        .build())
                .toList();
    }

    public WorkoutResponse getWorkout(long id) {
        Workout workout = workoutRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());

        return WorkoutResponse.builder()
                .id(workout.getId())
                .name(workout.getName())
                .muscleGroups(workout.getMuscleGroups())
                .build();
    }

    public void createWorkout(Workout workout) {
        if (workout == null || Strings.isBlank(workout.getName())) {
            throw new ValidationException("workout could not be null");
        }

        workoutRepository.save(workout);
    }

    public void updateWorkout(long id, Workout workout) {
        if (workout == null || Strings.isBlank(workout.getName())) {
            throw new ValidationException("workout could not be updated");
        }
        workout.setId(id);
        workoutRepository.save(workout);
    }

    public void deleteWorkout(long id) {
        Workout workout = workoutRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        workoutRepository.delete(workout);
    }
}
