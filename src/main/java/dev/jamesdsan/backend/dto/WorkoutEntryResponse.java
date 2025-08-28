package dev.jamesdsan.backend.dto;

import java.time.Instant;

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
public class WorkoutEntryResponse {
    private long id;
    private String workoutName;
    private String workoutDescription;

    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer duration;

    private Instant createdAt;
}
