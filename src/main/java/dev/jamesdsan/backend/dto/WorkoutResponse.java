package dev.jamesdsan.backend.dto;

import java.util.Set;

import dev.jamesdsan.backend.entity.MuscleGroup;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutResponse {
    private Long id;
    private String name;
    private String description;
    private Set<MuscleGroup> muscleGroups;
}
