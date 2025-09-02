package dev.jamesdsan.backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.jamesdsan.backend.dto.WorkoutEntryResponse;
import dev.jamesdsan.backend.dto.requests.WorkoutEntryRequest;
import dev.jamesdsan.backend.service.AuthenticatedUserService;
import dev.jamesdsan.backend.service.WorkoutEntryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/workoutentries")
@RequiredArgsConstructor
public class WorkoutEntryController {

    @Autowired
    private final WorkoutEntryService workoutEntryService;

    @Autowired
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping("")
    public List<WorkoutEntryResponse> getWorkoutEntrys(
            @RequestParam(required = false) LocalDate date,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return workoutEntryService.listWorkoutEntriesByUser(
                authenticatedUserService.getCurrentUser().getId(),
                date,
                pageable);
    }

    @GetMapping("/{workoutEntryId}")
    public WorkoutEntryResponse getWorkoutEntry(@PathVariable Long workoutEntryId) {
        return workoutEntryService.getWorkoutEntryByUser(
                authenticatedUserService.getCurrentUser().getId(),
                workoutEntryId);
    }

    @PostMapping("")
    public void createWorkoutEntry(@RequestBody WorkoutEntryRequest workoutEntryRequest) {
        workoutEntryService.createWorkoutEntry(
                authenticatedUserService.getCurrentUser().getId(),
                workoutEntryRequest.getWorkoutId(),
                workoutEntryRequest.getWorkoutEntry());
    }

    @PutMapping("/{workoutEntryId}")
    public void updateWorkoutEntry(@PathVariable long workoutEntryId,
            @RequestBody WorkoutEntryRequest workoutEntryRequest) {
        workoutEntryService.updateWorkoutEntry(
                authenticatedUserService.getCurrentUser().getId(),
                workoutEntryRequest.getWorkoutId(),
                workoutEntryId,
                workoutEntryRequest.getWorkoutEntry());
    }

    @DeleteMapping("/{workoutEntryId}")
    public void deleteWorkoutEntry(@PathVariable Long workoutEntryId) {
        workoutEntryService.deleteWorkoutEntry(
                authenticatedUserService.getCurrentUser().getId(),
                workoutEntryId);
    }
}
