package dev.jamesdsan.backend.repository;

import dev.jamesdsan.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
