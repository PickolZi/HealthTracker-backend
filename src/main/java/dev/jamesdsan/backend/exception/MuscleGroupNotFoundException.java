package dev.jamesdsan.backend.exception;

public class MuscleGroupNotFoundException extends RuntimeException {
    public MuscleGroupNotFoundException(Long id) {
        super("MuscleGroup with id " + id + " not found");
    }
}
