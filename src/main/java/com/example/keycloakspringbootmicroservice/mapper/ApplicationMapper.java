package com.example.keycloakspringbootmicroservice.mapper;

import com.example.keycloakspringbootmicroservice.domain.Organization;
import com.example.keycloakspringbootmicroservice.dto.OrganizationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    default OrganizationDTO organizationToOrganizationDto(Organization organization) {
        return OrganizationDTO.builder()
            .id(organization.getId())
            .name(organization.getName())
            .ownerId(organization.getOwner())
            .registrationDateTime(organization.getRegistrationDateTime())
            .build();
    }
}
