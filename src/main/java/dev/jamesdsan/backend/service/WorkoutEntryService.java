package dev.jamesdsan.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.dto.WorkoutEntryResponse;
import dev.jamesdsan.backend.entity.User;
import dev.jamesdsan.backend.entity.Workout;
import dev.jamesdsan.backend.entity.WorkoutEntry;
import dev.jamesdsan.backend.exception.UserNotFoundException;
import dev.jamesdsan.backend.exception.WorkoutEntryNotFoundException;
import dev.jamesdsan.backend.exception.WorkoutNotFoundException;
import dev.jamesdsan.backend.repository.UserRepository;
import dev.jamesdsan.backend.repository.WorkoutEntryRepository;
import dev.jamesdsan.backend.repository.WorkoutRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutEntryService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final WorkoutRepository workoutRepository;

    @Autowired
    private final WorkoutEntryRepository workoutEntryRepository;

    @Autowired
    private final AuthenticatedUserService authenticatedUserService;

    public List<WorkoutEntryResponse> listWorkoutEntriesByUser(long userId, LocalDate date, Pageable pageable) {
        logger.info("[WorkoutEntryService] listing workouts for user with id: {}", userId);

        authenticatedUserService.isUserAuthorizedElseThrowAccessDeniedException(userId);

        User user = findUserByIdElseThrowUserNotFoundException(userId);

        List<WorkoutEntry> fetchedWorkoutEntries = List.of();
        if (date == null) {
            logger.info("[WorkoutEntryService] listing all workouts across all dates");
            fetchedWorkoutEntries = workoutEntryRepository.findAllByUser(user);
        } else {
            logger.info("[WorkoutEntryService] listing all workouts on date: {}", date.toString());
            fetchedWorkoutEntries = workoutEntryRepository.findAllByUserAndDate(user, date, pageable);
        }

        List<WorkoutEntryResponse> workoutEntries = fetchedWorkoutEntries
                .stream()
                .map(workoutEntry -> {
                    return WorkoutEntryResponse.builder()
                            .id(workoutEntry.getId())
                            .workoutName(workoutEntry.getWorkout().getName())
                            .workoutDescription(workoutEntry.getWorkout().getDescription())
                            .sets(workoutEntry.getSets())
                            .reps(workoutEntry.getReps())
                            .weight(workoutEntry.getWeight())
                            .duration(workoutEntry.getDuration())
                            .date(workoutEntry.getDate())
                            .createdAt(workoutEntry.getCreatedAt())
                            .build();
                })
                .toList();

        logger.info(
                "[WorkoutEntryService] successfully found {} workout entries for user with id: {}",
                workoutEntries.size(), userId);
        return workoutEntries;
    }

    public WorkoutEntryResponse getWorkoutEntryByUser(long userId, long workoutEntryId) {
        logger.info("[WorkoutEntryService] getting workout entry with id: {} for user with id: {}", workoutEntryId,
                userId);

        authenticatedUserService.isUserAuthorizedElseThrowAccessDeniedException(userId);

        User user = findUserByIdElseThrowUserNotFoundException(userId);
        WorkoutEntry workoutEntry = findWorkoutEntryByIdElseThrowWorkoutNotFoundException(workoutEntryId);

        isUserAuthorizedToAccessWorkoutEntryElseThrowAccessDeniedException(user, workoutEntry);

        WorkoutEntryResponse workoutEntryResponse = WorkoutEntryResponse.builder()
                .id(workoutEntry.getId())
                .workoutName(workoutEntry.getWorkout().getName())
                .workoutDescription(workoutEntry.getWorkout().getDescription())
                .sets(workoutEntry.getSets())
                .reps(workoutEntry.getReps())
                .weight(workoutEntry.getWeight())
                .duration(workoutEntry.getDuration())
                .date(workoutEntry.getDate())
                .createdAt(workoutEntry.getCreatedAt())
                .build();

        logger.info("[WorkoutEntryService] successfully found workout entry with id: {} for user with id: {}",
                workoutEntryId, userId);
        return workoutEntryResponse;
    }

    public void createWorkoutEntry(long userId, long workoutId, WorkoutEntry workoutEntry) {
        logger.info("[WorkoutEntryService] creating workout entry for workout id: {} for user with id: {}", workoutId,
                userId);

        authenticatedUserService.isUserAuthorizedElseThrowAccessDeniedException(userId);

        User user = findUserByIdElseThrowUserNotFoundException(userId);
        Workout workout = getWorkoutByIdElseThrowWorkoutNotFoundException(workoutId);

        validateWorkoutEntry(workoutEntry);

        workoutEntry.setUser(user);
        workoutEntry.setWorkout(workout);
        try {
            WorkoutEntry createdWorkoutEntry = workoutEntryRepository.save(workoutEntry);
            logger.info(
                    "[WorkoutEntryService] successfully created workout entry with id: {} for workout: {} for user with id: {}",
                    createdWorkoutEntry.getId(), workout.getName(), user.getId());
        } catch (Exception exc) {
            logger.error("[WorkoutEntryService] failed to create workout entry for workout: {} for user with id: {}",
                    workout.getName(), user.getId());
            throw exc;
        }
    }

    public void updateWorkoutEntry(long userId, long workoutId, long workoutEntryId, WorkoutEntry workoutEntry) {
        logger.info("[WorkoutEntryService] updating workout entry");

        authenticatedUserService.isUserAuthorizedElseThrowAccessDeniedException(userId);

        User user = findUserByIdElseThrowUserNotFoundException(userId);
        Workout workout = getWorkoutByIdElseThrowWorkoutNotFoundException(workoutId);
        WorkoutEntry originalWorkoutEntry = findWorkoutEntryByIdElseThrowWorkoutNotFoundException(workoutEntryId);

        isUserAuthorizedToAccessWorkoutEntryElseThrowAccessDeniedException(user, originalWorkoutEntry);

        if (!originalWorkoutEntry.getUser().getId().equals(userId)) {
            logger.error(
                    "[WorkoutEntryService] failed to update workout entry with id: {} because the user with id: {} does not match the workout entry user id: {}",
                    originalWorkoutEntry.getId(), user.getId(), originalWorkoutEntry.getUser().getId());
            throw new ValidationException("This user can not update this workout entry as it does not belong to them");
        }

        validateWorkoutEntry(workoutEntry);

        try {
            workoutEntry.setWorkout(workout);
            WorkoutEntry mergedWorkoutEntry = originalWorkoutEntry.merge(workoutEntry);
            WorkoutEntry updatedWorkoutEntry = workoutEntryRepository.save(mergedWorkoutEntry);
            logger.info(
                    "[WorkoutEntryService] successfully updated workout entry with id: {} for workout: {} for user with id: {}",
                    updatedWorkoutEntry.getId(), updatedWorkoutEntry.getWorkout().getName(), user.getId());
        } catch (Exception exc) {
            logger.error("[WorkoutEntryService] failed to update workout entry for workout: {} for user with id: {}",
                    workout.getName(), user.getId());
            throw exc;
        }
    }

    public void deleteWorkoutEntry(long userId, long workoutEntryId) {
        logger.info("[WorkoutEntryService] deleting workout entry with id: {}", workoutEntryId);

        authenticatedUserService.isUserAuthorizedElseThrowAccessDeniedException(userId);

        User user = findUserByIdElseThrowUserNotFoundException(userId);
        WorkoutEntry workoutEntry = findWorkoutEntryByIdElseThrowWorkoutNotFoundException(workoutEntryId);

        isUserAuthorizedToAccessWorkoutEntryElseThrowAccessDeniedException(user, workoutEntry);

        if (!workoutEntry.getUser().getId().equals(userId)) {
            logger.error(
                    "[WorkoutEntryService] failed to delete workout entry with id: {} because the user with id: {} does not match the workout entry user id: {}",
                    workoutEntry.getId(), user.getId(), workoutEntry.getUser().getId());
            throw new ValidationException("This user can not delete this workout entry as it does not belong to them");
        }

        try {
            workoutEntryRepository.delete(workoutEntry);
            logger.info("[WorkoutEntryService] successfully deleted workout entry with id: {}", workoutEntryId);
        } catch (Exception exc) {
            logger.error("[WorkoutEntryService] failed to delete workout entry with id: {}", workoutEntryId);
            throw exc;
        }
    }

    private void validateWorkoutEntry(WorkoutEntry workoutEntry) {
        if (workoutEntry.getSets() == null || workoutEntry.getSets() <= 0) {
            logger.error(
                    "[WorkoutEntryService] failed to update workout entry with id: {} because sets are less than or equal to 0",
                    workoutEntry.getId());
            throw new ValidationException("Can not create new workout entry when sets are less than or equal to 0");
        }
        if (workoutEntry.getReps() == null || workoutEntry.getReps() <= 0) {
            logger.error(
                    "[WorkoutEntryService] failed to update workout entry with id: {} because reps are less than or equal to 0",
                    workoutEntry.getId());
            throw new ValidationException("Can not create new workout entry when reps are less than or equal to 0");
        }
        if (workoutEntry.getWeight() == null || workoutEntry.getWeight() <= 0) {
            logger.error(
                    "[WorkoutEntryService] failed to update workout entry with id: {} because weight are less than or equal to 0",
                    workoutEntry.getId());
            throw new ValidationException("Can not create new workout entry when weight are less than or equal to 0");
        }
        if (workoutEntry.getDuration() == null || workoutEntry.getDuration() < 0) {
            logger.error(
                    "[WorkoutEntryService] failed to update workout entry with id: {} because time are less than or equal to 0",
                    workoutEntry.getId());
            throw new ValidationException("Can not create new workout entry when time is less than 0");
        }
    }

    private User findUserByIdElseThrowUserNotFoundException(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            logger.error("[WorkoutEntryService] failed to find user with id: {}", userId);
            throw new UserNotFoundException(userId);
        }

        logger.info("[WorkoutEntryService] successfully found user with id: {}", userId);
        return user;
    }

    private Workout getWorkoutByIdElseThrowWorkoutNotFoundException(long workoutId) {
        Workout workout = workoutRepository.findById(workoutId).orElse(null);
        if (workout == null) {
            logger.error("[WorkoutEntryService] failed to find workout with id: {}", workoutId);
            throw new WorkoutNotFoundException(workoutId);
        }

        logger.info("[WorkoutEntryService] successfully found workout with id: {}", workoutId);
        return workout;
    }

    private WorkoutEntry findWorkoutEntryByIdElseThrowWorkoutNotFoundException(long workoutEntryId) {
        WorkoutEntry workoutEntry = workoutEntryRepository.findById(workoutEntryId).orElse(null);
        if (workoutEntry == null) {
            logger.error("[WorkoutEntryService] failed to find workout entry with id: {}", workoutEntryId);
            throw new WorkoutEntryNotFoundException(workoutEntryId);
        }

        logger.info("[WorkoutEntryService] successfully found workout entry with id: {}", workoutEntryId);
        return workoutEntry;
    }

    private void isUserAuthorizedToAccessWorkoutEntryElseThrowAccessDeniedException(User user,
            WorkoutEntry workoutEntry) {

        if (user == null ||
                workoutEntry == null ||
                workoutEntry.getUser() == null) {
            logger.error(
                    "[WorkoutEntryService] failed to access workout entry with id because of bad user: {} or bad workout entry passed: {}",
                    user,
                    workoutEntry);

            throw new AccessDeniedException(
                    String.format("User does not have access to this workout entry"));
        }

        if (workoutEntry.getUser().getId() != user.getId()) {
            logger.error(
                    "[WorkoutEntryService] failed to access workout entry with id: {} through user with id: {} because the owner of the workout entry has id: {}",
                    workoutEntry.getId(),
                    user.getId(),
                    workoutEntry.getUser().getId());

            throw new AccessDeniedException(
                    String.format(
                            "User with id: %d does not have access to workout entry with id: %d",
                            user.getId(),
                            workoutEntry.getId()));
        }
    }
}
