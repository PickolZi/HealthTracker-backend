package dev.jamesdsan.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

import org.apache.logging.log4j.util.Strings;

@Entity
@Table(name = "workout_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    private Integer sets;
    private Integer reps;
    private Double weight;
    private Integer duration;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDate.now();
        }
        createdAt = Instant.now();
    }

    public WorkoutEntry merge(WorkoutEntry other) {
        if (other.getWorkout() != null)
            this.setWorkout(other.getWorkout());
        if (other.getSets() != null)
            this.setSets(other.getSets());
        if (other.getReps() != null)
            this.setReps(other.getReps());
        if (other.getWeight() != null)
            this.setWeight(other.getWeight());
        if (other.getDuration() != null)
            this.setDuration(other.getDuration());
        if (other.getDate() != null)
            this.setDate(other.getDate());

        return this;
    }
}
