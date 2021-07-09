package com.example.keycloakspringbootmicroservice.rest.repositories;

import com.example.keycloakspringbootmicroservice.domain.User;
import com.example.keycloakspringbootmicroservice.domain.enums.Status;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsById(UUID id);

    boolean existsByEmailAndStatus(String email, Status status);

}
