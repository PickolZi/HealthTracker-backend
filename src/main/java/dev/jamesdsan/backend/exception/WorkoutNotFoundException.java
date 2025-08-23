package dev.jamesdsan.backend.exception;

public class WorkoutNotFoundException extends RuntimeException {
    public WorkoutNotFoundException(Long id) {
        super("Workout with id " + id + " not found");
    }
}
