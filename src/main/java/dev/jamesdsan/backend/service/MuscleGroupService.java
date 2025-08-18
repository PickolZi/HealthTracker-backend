package dev.jamesdsan.backend.service;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.dto.MuscleGroupResponse;
import dev.jamesdsan.backend.entity.MuscleGroup;
import dev.jamesdsan.backend.repository.MuscleGroupRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MuscleGroupService {
    private final MuscleGroupRepository muscleGroupRepository;

    public List<MuscleGroupResponse> listMuscleGroups() {
        return muscleGroupRepository.findAll()
                .stream()
                .map(muscleGroup -> MuscleGroupResponse.builder()
                        .id(muscleGroup.getId())
                        .name(muscleGroup.getName())
                        .workouts(muscleGroup.getWorkouts())
                        .build())
                .toList();
    }

    public MuscleGroupResponse getMuscleGroup(long id) {
        MuscleGroup muscleGroup = muscleGroupRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());

        return MuscleGroupResponse.builder()
                .id(muscleGroup.getId())
                .name(muscleGroup.getName())
                .workouts(muscleGroup.getWorkouts())
                .build();
    }

    public void createMuscleGroup(MuscleGroup muscleGroup) {
        if (muscleGroup == null || Strings.isBlank(muscleGroup.getName())) {
            throw new ValidationException("muscleGroup could not be null");
        }

        muscleGroupRepository.save(muscleGroup);
    }

    public void updateMuscleGroup(long id, MuscleGroup muscleGroup) {
        if (muscleGroup == null || Strings.isBlank(muscleGroup.getName())) {
            throw new ValidationException("muscleGroup could not be updated");
        }
        muscleGroup.setId(id);
        muscleGroupRepository.save(muscleGroup);
    }

    public void deleteMuscleGroup(long id) {
        MuscleGroup muscleGroup = muscleGroupRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        muscleGroupRepository.delete(muscleGroup);
    }
}
