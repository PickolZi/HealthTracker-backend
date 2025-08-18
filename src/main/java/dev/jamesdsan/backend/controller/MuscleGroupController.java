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

import dev.jamesdsan.backend.dto.MuscleGroupResponse;
import dev.jamesdsan.backend.entity.MuscleGroup;
import dev.jamesdsan.backend.service.MuscleGroupService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/musclegroups")
@RequiredArgsConstructor
public class MuscleGroupController {

    private final MuscleGroupService muscleGroupService;

    @GetMapping("")
    public List<MuscleGroupResponse> getMuscleGroups() {
        return muscleGroupService.listMuscleGroups();
    }

    @GetMapping("/{muscleGroupId}")
    public MuscleGroupResponse getMuscleGroup(@PathVariable Long muscleGroupId) {
        return muscleGroupService.getMuscleGroup(muscleGroupId);
    }

    @PostMapping("")
    public void createMuscleGroup(@RequestBody MuscleGroup muscleGroup) {
        muscleGroupService.createMuscleGroup(muscleGroup);
    }

    @PutMapping("/{muscleGroupId}")
    public void updateMuscleGroup(@PathVariable long muscleGroupId, @RequestBody MuscleGroup muscleGroup) {
        muscleGroupService.updateMuscleGroup(muscleGroupId, muscleGroup);
    }

    @DeleteMapping("/{muscleGroupId}")
    public void deleteMuscleGroup(@PathVariable Long muscleGroupId) {
        muscleGroupService.deleteMuscleGroup(muscleGroupId);
    }
}
