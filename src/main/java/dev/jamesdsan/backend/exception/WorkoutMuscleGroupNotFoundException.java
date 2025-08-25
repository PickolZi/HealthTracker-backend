package dev.jamesdsan.backend.exception;

public class WorkoutMuscleGroupNotFoundException extends RuntimeException {
    public WorkoutMuscleGroupNotFoundException(Long workoutId, long muscleGroupId) {
        super("WorkoutMuscleGroup with workoutId: " + workoutId + " and muscleGroupId: " + muscleGroupId
                + " not found");
    }
}
