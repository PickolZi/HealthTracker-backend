package dev.jamesdsan.backend.exception;

public class WorkoutEntryNotFoundException extends RuntimeException {
    public WorkoutEntryNotFoundException(Long id) {
        super("Workout entry with id " + id + " not found");
    }
}