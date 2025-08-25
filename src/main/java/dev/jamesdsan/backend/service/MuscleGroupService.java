package dev.jamesdsan.backend.service;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import dev.jamesdsan.backend.dto.MuscleGroupResponse;
import dev.jamesdsan.backend.entity.MuscleGroup;
import dev.jamesdsan.backend.exception.MuscleGroupNotFoundException;
import dev.jamesdsan.backend.repository.MuscleGroupRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MuscleGroupService {
    private static final Logger logger = LoggerFactory.getLogger(MuscleGroup.class);
    private final MuscleGroupRepository muscleGroupRepository;

    public List<MuscleGroupResponse> listMuscleGroups() {
        logger.info("[MuscleGroupService] listing muscle groups");

        List<MuscleGroup> muscleGroups = muscleGroupRepository.findAll();

        logger.info("[MuscleGroupService] found {} muscle groups", muscleGroups.size());

        return muscleGroups
                .stream()
                .map(muscleGroup -> MuscleGroupResponse.builder()
                        .id(muscleGroup.getId())
                        .name(muscleGroup.getName())
                        .workouts(muscleGroup.getWorkouts())
                        .build())
                .toList();
    }

    public MuscleGroupResponse getMuscleGroup(long muscleGroupId) {
        logger.info("[MuscleGroupService] fetching muscle group with id: {}", muscleGroupId);
        MuscleGroup muscleGroup = findMuscleGroupByIdElseThrowMuscleGroupNotFoundException(muscleGroupId);

        logger.info("[MuscleGroupService] found muscle with id: {} and name: {}",
                muscleGroup.getId(),
                muscleGroup.getName());

        return MuscleGroupResponse.builder()
                .id(muscleGroup.getId())
                .name(muscleGroup.getName())
                .workouts(muscleGroup.getWorkouts())
                .build();
    }

    public void createMuscleGroup(MuscleGroup muscleGroup) {
        logger.info("[MuscleGroupService] creating muscle group");

        if (muscleGroup == null || Strings.isBlank(muscleGroup.getName())) {
            logger.error("[MuscleGroupService] failed to create muscle group because it was null");
            throw new ValidationException("muscleGroup could not be null");
        }

        try {
            MuscleGroup createdMuscleGroup = muscleGroupRepository.save(muscleGroup);
            logger.info(
                    "[MuscleGroupService] successfully created muscle group with id: and name: {}",
                    createdMuscleGroup.getId(),
                    createdMuscleGroup.getName());
        } catch (DataIntegrityViolationException exc) {
            logger.error("[MuscleGroupService] failed to create muscle group due to naming conflicts in the db");
            throw new ValidationException("Failed to create muscle group because it already exists");
        } catch (Exception exc) {
            logger.error("[MuscleGroupService] failed to create muscle group due unknown reason");
            throw new ValidationException("Failed to create muscle group because of bad request");
        }
    }

    public void updateMuscleGroup(long muscleGroupId, MuscleGroup muscleGroup) {
        logger.info("[MuscleGroupService] updating muscle group with id: {}", muscleGroupId);
        if (muscleGroup == null || Strings.isBlank(muscleGroup.getName())) {
            logger.error("[MuscleGroupService] failed to update muscle group because muscle group is null");
            throw new ValidationException("muscleGroup could not be updated");
        }

        MuscleGroup curMuscleGroup = findMuscleGroupByIdElseThrowMuscleGroupNotFoundException(muscleGroupId);

        MuscleGroup mergedMuscleGroup = curMuscleGroup.merge(muscleGroup);

        try {
            muscleGroupRepository.save(mergedMuscleGroup);
            logger.info("[MuscleGroupService] successfully updated muscle group with id: {}",
                    mergedMuscleGroup.getId());
        } catch (DataIntegrityViolationException exc) {
            logger.error("[MuscleGroupService] failed to update muscle group with id: {} because it already exists",
                    mergedMuscleGroup.getId());
            throw new ValidationException("Failed to update muscle group because this muscle group already exists.");
        } catch (Exception exc) {
            logger.error("[MuscleGroupService] failed to update muscle group with id: {}", mergedMuscleGroup.getId());
            throw new ValidationException("Failed to update muscle group");
        }
    }

    public void deleteMuscleGroup(long muscleGroupId) {
        logger.info("[MuscleGroupService] deleting muscle group with id: {}", muscleGroupId);
        MuscleGroup muscleGroup = findMuscleGroupByIdElseThrowMuscleGroupNotFoundException(muscleGroupId);

        try {
            muscleGroupRepository.delete(muscleGroup);
            logger.info("[MuscleGroupService] succesfully deleted muscle group with id: {}", muscleGroupId);
        } catch (Exception exc) {
            logger.error("[MuscleGroupService] failed to delete muscle group with id: {}", muscleGroupId);
            throw new ValidationException("Failed to delete muscle group");
        }
    }

    private MuscleGroup findMuscleGroupByIdElseThrowMuscleGroupNotFoundException(long muscleGroupId) {
        MuscleGroup muscleGroup = muscleGroupRepository.findById(muscleGroupId).orElse(null);

        if (muscleGroup == null) {
            logger.error("[MuscleGroupService] failed to find muscle group with id: {}", muscleGroupId);
            throw new MuscleGroupNotFoundException(muscleGroupId);
        }

        logger.info("[MuscleGroupService] successfully found muscle group with id: {} and name: {}",
                muscleGroup.getId(), muscleGroup.getName());

        return muscleGroup;
    }
}
