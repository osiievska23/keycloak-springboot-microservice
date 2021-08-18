package com.example.keycloakspringbootmicroservice.rest.repositories;

import com.example.keycloakspringbootmicroservice.domain.Group;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    List<Group> findAllByOrganizationId(UUID organizationId);
}
