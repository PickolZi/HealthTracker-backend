package dev.jamesdsan.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.dto.WorkoutEntryResponse;
import dev.jamesdsan.backend.entity.User;
import dev.jamesdsan.backend.entity.Workout;
import dev.jamesdsan.backend.entity.WorkoutEntry;
import dev.jamesdsan.backend.repository.UserRepository;
import dev.jamesdsan.backend.repository.WorkoutEntryRepository;
import dev.jamesdsan.backend.repository.WorkoutRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutEntryService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final WorkoutRepository workoutRepository;

    @Autowired
    private final WorkoutEntryRepository workoutEntryRepository;

    public List<WorkoutEntryResponse> listWorkoutEntriesByUser(long userId) {
        User user = getUserById(userId);

        List<WorkoutEntryResponse> workoutEntries = workoutEntryRepository.findAllByUser(user)
                .stream()
                .map(workoutEntry -> {
                    return WorkoutEntryResponse.builder()
                            .workoutName(workoutEntry.getWorkout().getName())
                            .workoutDescription(workoutEntry.getWorkout().getDescription())
                            .sets(workoutEntry.getSets())
                            .reps(workoutEntry.getReps())
                            .weight(workoutEntry.getWeight())
                            .duration(workoutEntry.getDuration())
                            .createdAt(workoutEntry.getCreatedAt())
                            .build();
                })
                .toList();
        return workoutEntries;
    }

    public WorkoutEntryResponse getWorkoutEntryByUser(long userId, long workoutEntryId) {
        // TODO: Validation to ensure workoutEntry user is same as user.
        // User user = getUserById(userId);
        WorkoutEntry workoutEntry = workoutEntryRepository.findById(workoutEntryId).orElseThrow(
                () -> new ValidationException("Workout entry could not be found when trying to get workout entry"));

        WorkoutEntryResponse workoutEntryResponse = WorkoutEntryResponse.builder()
                .workoutName(workoutEntry.getWorkout().getName())
                .workoutDescription(workoutEntry.getWorkout().getDescription())
                .sets(workoutEntry.getSets())
                .reps(workoutEntry.getReps())
                .weight(workoutEntry.getWeight())
                .duration(workoutEntry.getDuration())
                .createdAt(workoutEntry.getCreatedAt())
                .build();

        return workoutEntryResponse;
    }

    public void createWorkoutEntry(long userId, long workoutId, WorkoutEntry workoutEntry) {
        User user = getUserById(userId);
        Workout workout = getWorkoutById(workoutId);

        validateWorkoutEntry(workoutEntry);

        workoutEntry.setUser(user);
        workoutEntry.setWorkout(workout);
        workoutEntryRepository.save(workoutEntry);
    }

    public void updateWorkoutEntry(long userId, long workoutId, long workoutEntryId, WorkoutEntry workoutEntry) {
        User user = getUserById(userId);
        Workout workout = getWorkoutById(workoutId);
        WorkoutEntry originalWorkoutEntry = getWorkoutEntryById(workoutEntryId);

        if (!originalWorkoutEntry.getUser().getId().equals(userId)) {
            throw new ValidationException("This user can not update this workout entry as it does not belong to them");
        }

        validateWorkoutEntry(workoutEntry);

        workoutEntry.setUser(user);
        workoutEntry.setWorkout(workout);
        workoutEntryRepository.save(workoutEntry);
    }

    public void deleteWorkoutEntry(long userId, long workoutEntryId) {
        User user = getUserById(userId);
        WorkoutEntry workoutEntry = getWorkoutEntryById(workoutEntryId);

        if (!workoutEntry.getUser().getId().equals(userId)) {
            throw new ValidationException("This user can not delete this workout entry as it does not belong to them");
        }

        workoutEntryRepository.delete(workoutEntry);
    }

    private void validateWorkoutEntry(WorkoutEntry workoutEntry) {
        if (workoutEntry.getSets() == null || workoutEntry.getSets() <= 0) {
            throw new ValidationException("Can not create new workout entry when sets are less than or equal to 0");
        }
        if (workoutEntry.getReps() == null || workoutEntry.getReps() <= 0) {
            throw new ValidationException("Can not create new workout entry when sets are less than or equal to 0");
        }
        if (workoutEntry.getWeight() == null || workoutEntry.getWeight() <= 0) {
            throw new ValidationException("Can not create new workout entry when sets are less than or equal to 0");
        }
        if (workoutEntry.getDuration() == null || workoutEntry.getDuration() < 0) {
            throw new ValidationException("Can not create new workout entry when sets are less than 0");
        }
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ValidationException("User could not be found"));
    }

    private Workout getWorkoutById(long workoutId) {
        return workoutRepository.findById(workoutId).orElseThrow(
                () -> new ValidationException("Workout could not be found"));
    }

    private WorkoutEntry getWorkoutEntryById(long workoutEntryId) {
        return workoutEntryRepository.findById(workoutEntryId).orElseThrow(
                () -> new ValidationException("Workout entry could not be found"));
    }
}
