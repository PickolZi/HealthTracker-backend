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

import dev.jamesdsan.backend.dto.WorkoutEntryResponse;
import dev.jamesdsan.backend.dto.requests.WorkoutEntryRequest;
import dev.jamesdsan.backend.service.WorkoutEntryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/workoutentries")
@RequiredArgsConstructor
public class WorkoutEntryController {

    private final WorkoutEntryService workoutEntryService;

    @GetMapping("")
    public List<WorkoutEntryResponse> getWorkoutEntrys() {
        return workoutEntryService.listWorkoutEntriesByUser(11L);
    }

    @GetMapping("/{workoutEntryId}")
    public WorkoutEntryResponse getWorkoutEntry(@PathVariable Long workoutEntryId) {
        return workoutEntryService.getWorkoutEntryByUser(11L, workoutEntryId);
    }

    @PostMapping("")
    public void createWorkoutEntry(@RequestBody WorkoutEntryRequest workoutEntryRequest) {
        workoutEntryService.createWorkoutEntry(
                11L,
                workoutEntryRequest.getWorkoutId(),
                workoutEntryRequest.getWorkoutEntry());
    }

    @PutMapping("/{workoutEntryId}")
    public void updateWorkoutEntry(@PathVariable long workoutEntryId,
            @RequestBody WorkoutEntryRequest workoutEntryRequest) {
        workoutEntryService.updateWorkoutEntry(
                11L,
                workoutEntryRequest.getWorkoutId(),
                workoutEntryId,
                workoutEntryRequest.getWorkoutEntry());
    }

    @DeleteMapping("/{workoutEntryId}")
    public void deleteWorkoutEntry(@PathVariable Long workoutEntryId) {
        workoutEntryService.deleteWorkoutEntry(11L, workoutEntryId);
    }
}
