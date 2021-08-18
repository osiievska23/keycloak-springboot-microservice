package com.example.keycloakspringbootmicroservice.services;

import com.example.keycloakspringbootmicroservice.dto.OrganizationDTO;
import com.example.keycloakspringbootmicroservice.rest.requests.CreateOrganizationRequest;
import java.util.UUID;

public interface OrganizationService {

    OrganizationDTO createOrganization(CreateOrganizationRequest request, String ownerEmail);

    OrganizationDTO getOrganizationById(UUID organizationId);
}
