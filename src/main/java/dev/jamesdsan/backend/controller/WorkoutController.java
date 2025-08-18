package dev.jamesdsan.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.jamesdsan.backend.dto.WorkoutResponse;
import dev.jamesdsan.backend.entity.Workout;
import dev.jamesdsan.backend.service.WorkoutService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @GetMapping("")
    public List<WorkoutResponse> getWorkouts() {
        return workoutService.listWorkouts();
    }

    @GetMapping("/{workoutId}")
    public WorkoutResponse getWorkout(@PathVariable Long workoutId) {
        return workoutService.getWorkout(workoutId);
    }

    @PostMapping("")
    public void createWorkout(@RequestBody Workout workout) {
        workoutService.createWorkout(workout);
    }

    @PutMapping("/{workoutId}")
    public void updateWorkout(@PathVariable long workoutId, @RequestBody Workout workout) {
        workoutService.updateWorkout(workoutId, workout);
    }

    @DeleteMapping("/{workoutId}")
    public void deleteWorkout(@PathVariable Long workoutId) {
        workoutService.deleteWorkout(workoutId);
    }
}
