package com.example.keycloakspringbootmicroservice.rest.repositories;

import com.example.keycloakspringbootmicroservice.domain.Organization;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    boolean existsByName(String name);

}
