package dev.jamesdsan.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<WorkoutEntryResponse>> getWorkoutEntrys(
            @RequestParam(required = false) LocalDate date,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        List<WorkoutEntryResponse> workoutEntries = workoutEntryService.listWorkoutEntriesByUser(
                authenticatedUserService.getCurrentUser().getId(),
                date,
                pageable);

        return ResponseEntity.ok(workoutEntries);
    }

    @GetMapping("/{workoutEntryId}")
    public ResponseEntity<WorkoutEntryResponse> getWorkoutEntry(@PathVariable Long workoutEntryId) {
        WorkoutEntryResponse workoutEntry = workoutEntryService.getWorkoutEntryByUser(
                authenticatedUserService.getCurrentUser().getId(),
                workoutEntryId);

        return ResponseEntity.ok(workoutEntry);
    }

    @PostMapping("")
    public ResponseEntity<WorkoutEntryResponse> createWorkoutEntry(
            @RequestBody WorkoutEntryRequest workoutEntryRequest) {
        WorkoutEntryResponse workoutEntry = workoutEntryService.createWorkoutEntry(
                authenticatedUserService.getCurrentUser().getId(),
                workoutEntryRequest.getWorkoutId(),
                workoutEntryRequest.getWorkoutEntry());

        return ResponseEntity.status(201).body(workoutEntry);
    }

    @PutMapping("/{workoutEntryId}")
    public ResponseEntity<WorkoutEntryResponse> updateWorkoutEntry(@PathVariable long workoutEntryId,
            @RequestBody WorkoutEntryRequest workoutEntryRequest) {
        WorkoutEntryResponse workoutEntry = workoutEntryService.updateWorkoutEntry(
                authenticatedUserService.getCurrentUser().getId(),
                workoutEntryRequest.getWorkoutId(),
                workoutEntryId,
                workoutEntryRequest.getWorkoutEntry());

        return ResponseEntity.ok(workoutEntry);
    }

    @DeleteMapping("/{workoutEntryId}")
    public ResponseEntity<Map<String, Long>> deleteWorkoutEntry(@PathVariable Long workoutEntryId) {
        workoutEntryService.deleteWorkoutEntry(
                authenticatedUserService.getCurrentUser().getId(),
                workoutEntryId);

        return ResponseEntity.ok(Map.of("workoutEntryId", workoutEntryId));
    }
}
