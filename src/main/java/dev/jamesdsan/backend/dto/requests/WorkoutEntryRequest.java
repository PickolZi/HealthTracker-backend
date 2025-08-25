package dev.jamesdsan.backend.dto.requests;

import dev.jamesdsan.backend.entity.WorkoutEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutEntryRequest {
    private WorkoutEntry workoutEntry;
    private long workoutId;
}
