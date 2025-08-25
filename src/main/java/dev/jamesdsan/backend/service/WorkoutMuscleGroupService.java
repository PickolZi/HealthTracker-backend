package dev.jamesdsan.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.entity.MuscleGroup;
import dev.jamesdsan.backend.entity.Workout;
import dev.jamesdsan.backend.exception.MuscleGroupNotFoundException;
import dev.jamesdsan.backend.exception.WorkoutMuscleGroupNotFoundException;
import dev.jamesdsan.backend.exception.WorkoutNotFoundException;
import dev.jamesdsan.backend.repository.MuscleGroupRepository;
import dev.jamesdsan.backend.repository.WorkoutRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutMuscleGroupService {
    private static final Logger logger = LoggerFactory.getLogger(WorkoutMuscleGroupService.class);

    @Autowired
    private final WorkoutRepository workoutRepository;

    @Autowired
    private final MuscleGroupRepository muscleGroupRepository;

    public void createLinkWorkoutToMuscleGroup(long workoutId, long muscleGroupId) {
        logger.info("[WorkoutMuscleGroupService] creating link between workoutId: {} and muscleGroupId: {}", workoutId,
                muscleGroupId);

        Workout workout = findWorkoutByIdElseThrowWorkoutNotFoundException(workoutId);
        MuscleGroup muscleGroup = findMuscleGroupByIdElseThrowWorkoutNotFoundException(muscleGroupId);

        workout.getMuscleGroups().add(muscleGroup);
        muscleGroup.getWorkouts().add(workout);

        try {
            workoutRepository.save(workout);
        } catch (DataIntegrityViolationException exc) {
            logger.error(
                    "[WorkoutMuscleGroupService] failed to create link between workout with id: {} and muscleGroup with id: {} probably because it already exists",
                    workoutId, muscleGroupId);
            throw new ValidationException(
                    "Failed to create link because of bad request probably because it already exists");
        } catch (Exception exc) {
            logger.error(
                    "[WorkoutMuscleGroupService] failed to create link between workout with id: {} and muscleGroup with id: {}",
                    workoutId, muscleGroupId);
            throw new ValidationException("Failed to create link because of bad request");
        }
    }

    public void deleteLinkWorkoutToMuscleGroup(long workoutId, long muscleGroupId) {
        logger.info("[WorkoutMuscleGroupService] deleting link between workoutId: {} and muscleGroupId: {}", workoutId,
                muscleGroupId);

        Workout workout = findWorkoutByIdElseThrowWorkoutNotFoundException(workoutId);
        MuscleGroup muscleGroup = findMuscleGroupByIdElseThrowWorkoutNotFoundException(muscleGroupId);

        if (!workout.getMuscleGroups().contains(muscleGroup) || !muscleGroup.getWorkouts().contains(workout)) {
            logger.error(
                    "[WorkoutMuscleGroupService] failed to delete a link between workout with id: {} and muscleGroup with id: {} because a workout or muscleGroup does not exist within one another.",
                    workoutId, muscleGroupId);
            throw new WorkoutMuscleGroupNotFoundException(workoutId, muscleGroupId);
        }
        workout.getMuscleGroups().remove(muscleGroup);
        muscleGroup.getWorkouts().remove(workout);

        try {
            workoutRepository.save(workout);
            logger.info(
                    "[WorkoutMuscleGroupService] successfully deleted link between workoutId: {} and muscleGroupId: {}",
                    workoutId, muscleGroupId);
        } catch (Exception exc) {
            logger.error(
                    "[WorkoutMuscleGroupService] failed to delete link between workoutId: {} and muscleGroupId: {} because of an unknown error");
            throw exc;
        }
    }

    private Workout findWorkoutByIdElseThrowWorkoutNotFoundException(long workoutId) {
        Workout workout = workoutRepository.findById(workoutId).orElse(null);
        if (workout == null) {
            logger.error("[MuscleWorkoutGroupService] failed to find workout with id: {}", workoutId);
            throw new WorkoutNotFoundException(workoutId);
        }

        logger.info("[MuscleWorkoutGroupService] successfully found workout with id: {}", workoutId);
        return workout;
    }

    private MuscleGroup findMuscleGroupByIdElseThrowWorkoutNotFoundException(long muscleGroupId) {
        MuscleGroup muscleGroup = muscleGroupRepository.findById(muscleGroupId).orElse(null);
        if (muscleGroup == null) {
            logger.error("[MuscleWorkoutGroupService] failed to find muscleGroup with id: {}", muscleGroupId);
            throw new MuscleGroupNotFoundException(muscleGroupId);
        }

        logger.info("[MuscleWorkoutGroupService] successfully found muscleGroup with id: {}", muscleGroupId);
        return muscleGroup;
    }

}
