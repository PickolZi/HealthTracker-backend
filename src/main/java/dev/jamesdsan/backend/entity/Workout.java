package dev.jamesdsan.backend.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workouts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    private String description;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JsonIgnore
    @JoinTable(name = "workout_musclegroup", joinColumns = @JoinColumn(name = "workout_id"), inverseJoinColumns = @JoinColumn(name = "musclegroup_id"))
    private Set<MuscleGroup> muscleGroups = new HashSet<>();

    public Workout merge(Workout other) {
        if (other.getName() != null)
            this.setName(other.getName());
        if (other.getDescription() != null)
            this.setDescription(other.getDescription());

        return this;
    }
}
