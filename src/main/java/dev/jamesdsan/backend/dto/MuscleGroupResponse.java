package dev.jamesdsan.backend.dto;

import java.util.Set;

import dev.jamesdsan.backend.entity.Workout;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MuscleGroupResponse {
    private Long id;
    private String name;
    private Set<Workout> workouts;
}
