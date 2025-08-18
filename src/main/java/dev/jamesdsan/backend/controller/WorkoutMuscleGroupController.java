package dev.jamesdsan.backend.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.jamesdsan.backend.service.WorkoutMuscleGroupService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/workoutmusclegroup")
@RequiredArgsConstructor
public class WorkoutMuscleGroupController {

    private final WorkoutMuscleGroupService workoutMuscleGroupService;

    @PostMapping("")
    public void createWorkoutMuscleGroupLink(@RequestBody WorkoutMuscleGroup workoutMuscleGroup) {
        workoutMuscleGroupService.createLinkWorkoutToMuscleGroup(workoutMuscleGroup.workoutId,
                workoutMuscleGroup.muscleGroupId);
    }

    @DeleteMapping("")
    public void deleteWorkoutMuscleGroupLink(@RequestBody WorkoutMuscleGroup workoutMuscleGroup) {
        workoutMuscleGroupService.deleteLinkWorkoutToMuscleGroup(workoutMuscleGroup.workoutId,
                workoutMuscleGroup.muscleGroupId);
    }
}

final class WorkoutMuscleGroup {
    public long workoutId;
    public long muscleGroupId;
}
