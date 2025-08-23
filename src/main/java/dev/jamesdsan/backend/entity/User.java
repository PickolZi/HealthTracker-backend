package dev.jamesdsan.backend.entity;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String provider;
    private String providerId;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public User merge(User other) {
        // Overwrites the current object with the User passed into merge()
        if (other.getUsername() != null)
            this.setUsername(other.getUsername());
        if (other.getEmail() != null)
            this.setEmail(other.getEmail());
        if (other.getPassword() != null)
            this.setPassword(other.getPassword());

        return this;
    }
}
