package dev.jamesdsan.backend.service;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.dto.WorkoutResponse;
import dev.jamesdsan.backend.entity.Workout;
import dev.jamesdsan.backend.exception.WorkoutNotFoundException;
import dev.jamesdsan.backend.repository.WorkoutRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutService {
    private static final Logger logger = LoggerFactory.getLogger(WorkoutService.class);

    private final WorkoutRepository workoutRepository;

    public List<WorkoutResponse> listWorkouts() {
        logger.info("[WorkoutService] listing workouts");

        List<Workout> workouts = workoutRepository.findAll();
        logger.info("[WorkoutService] found {} workouts", workouts.size());

        return workouts
                .stream()
                .map(workout -> WorkoutResponse.builder()
                        .id(workout.getId())
                        .name(workout.getName())
                        .description(workout.getDescription())
                        .muscleGroups(workout.getMuscleGroups())
                        .build())
                .toList();
    }

    public WorkoutResponse getWorkout(long workoutId) {
        logger.info("[WorkoutService] getting workout for id: {}", workoutId);

        Workout workout = findWorkoutByIdElseThrowWorkoutNotFound(workoutId);

        return WorkoutResponse.builder()
                .id(workout.getId())
                .name(workout.getName())
                .description(workout.getDescription())
                .muscleGroups(workout.getMuscleGroups())
                .build();
    }

    public void createWorkout(Workout workout) {
        logger.info("[WorkoutService] creating workout");

        if (workout == null || Strings.isBlank(workout.getName())) {
            logger.error("[WorkoutService] failed to create workout because workout is null");
            throw new ValidationException("workout could not be null");
        }

        try {
            Workout createdWorkout = workoutRepository.save(workout);
            logger.info("[WorkoutService] successfully created workout with id: {} and name: {}",
                    createdWorkout.getId(),
                    createdWorkout.getName());
        } catch (DataIntegrityViolationException exc) {
            logger.error("[WorkoutService] failed to create workout because of conflicts within db");
            throw new ValidationException("Failed to create workout because it probably already exists");
        } catch (Exception exc) {
            logger.error("[WorkoutService] failed to create workout because of an unknown reason");
            throw new ValidationException("Failed to create workout because of a bad request");
        }
    }

    public void updateWorkout(long workoutId, Workout workout) {
        logger.info("[WorkoutService] updating workout");

        Workout curWorkout = findWorkoutByIdElseThrowWorkoutNotFound(workoutId);

        if (workout == null || Strings.isBlank(workout.getName())) {
            logger.info("[WorkoutService] failed to update workout because workout is null");
            throw new ValidationException("workout could not be updated");
        }

        try {
            Workout mergedWorkout = curWorkout.merge(workout);
            workoutRepository.save(mergedWorkout);
            logger.info("[WorkoutService] successfully updated workout with id: {} and name {}", mergedWorkout.getId(),
                    mergedWorkout.getName());
        } catch (DataIntegrityViolationException exc) {
            logger.error("[WorkoutService] failed to update workout because of conflicts within the db");
            throw new ValidationException("Failed to update workout because it probably already exists");
        } catch (Exception exc) {
            logger.error("[WorkoutService] failed to update workout because of an unknown reason");
            throw new ValidationException("Failed to update workout because of a bad request");
        }
    }

    public void deleteWorkout(long workoutId) {
        logger.info("[WorkoutService] deleting workout with id: {}", workoutId);

        Workout workout = findWorkoutByIdElseThrowWorkoutNotFound(workoutId);

        try {
            workoutRepository.delete(workout);
            logger.info("[WorkoutService] successfully deleted workout with id: {}", workoutId);
        } catch (Exception exc) {
            logger.error("[WorkoutService] failed to delete workout with id: {}", workoutId);
            throw exc;
        }

    }

    private Workout findWorkoutByIdElseThrowWorkoutNotFound(long workoutId) {
        Workout workout = workoutRepository.findById(workoutId).orElse(null);
        if (workout == null) {
            logger.error("[WorkoutService] failed to find workout with id: {}", workoutId);
            throw new WorkoutNotFoundException(workoutId);
        }

        logger.info("[WorkoutService] successfully found workout with id: {}", workoutId);
        return workout;
    }
}
