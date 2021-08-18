package com.example.keycloakspringbootmicroservice.services.impl;

import static com.example.keycloakspringbootmicroservice.constants.ExceptionConstants.CREDENTIALS_INVALID_EXCEPTION;

import com.example.keycloakspringbootmicroservice.domain.Group;
import com.example.keycloakspringbootmicroservice.domain.Organization;
import com.example.keycloakspringbootmicroservice.domain.User;
import com.example.keycloakspringbootmicroservice.dto.GroupDTO;
import com.example.keycloakspringbootmicroservice.dto.OrganizationDTO;
import com.example.keycloakspringbootmicroservice.dto.UserDTO;
import com.example.keycloakspringbootmicroservice.mapper.ApplicationMapper;
import com.example.keycloakspringbootmicroservice.rest.repositories.GroupRepository;
import com.example.keycloakspringbootmicroservice.rest.repositories.OrganizationRepository;
import com.example.keycloakspringbootmicroservice.rest.repositories.UserRepository;
import com.example.keycloakspringbootmicroservice.rest.requests.CreateOrganizationRequest;
import com.example.keycloakspringbootmicroservice.services.OrganizationService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final RealmResource realmResource;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ApplicationMapper applicationMapper;
    private final ModelMapper modelMapper;

    public OrganizationServiceImpl(RealmResource realmResource,
        OrganizationRepository organizationRepository,
        UserRepository userRepository, GroupRepository groupRepository,
        ApplicationMapper applicationMapper, ModelMapper modelMapper) {
        this.realmResource = realmResource;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.applicationMapper = applicationMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrganizationDTO createOrganization(CreateOrganizationRequest request, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new BadRequestException(CREDENTIALS_INVALID_EXCEPTION));

        Organization organization = Organization.builder()
            .name(request.getName())
            .owner(owner.getId())
            .registrationDateTime(Instant.now())
            .users(Collections.singleton(owner))
            .build();
        organizationRepository.save(organization);

        List<GroupRepresentation> defaultGroups = getDefaultOrganizationIdGroups(organization);
        for (GroupRepresentation gr : defaultGroups) {
            Group group = Group.builder()
                .organization(organization)
                .name(gr.getName())
                .keycloakGroupId(gr.getId())
                .users(Collections.singleton(owner))
                .build();

            groupRepository.save(group);
        }

        return applicationMapper.organizationToOrganizationDto(organization);
    }

    @Override
    public OrganizationDTO getOrganizationById(UUID organizationIdId) {
        Organization organization = organizationRepository.findById(organizationIdId)
            .orElseThrow(() -> new NotFoundException(CREDENTIALS_INVALID_EXCEPTION));

        OrganizationDTO result = modelMapper.map(organization, OrganizationDTO.class);
        Set<GroupDTO> groupDTOs = groupRepository.findAllByOrganizationId(organization.getId()).stream()
            .map(g -> modelMapper.map(g, GroupDTO.class))
            .collect(Collectors.toSet());
        result.setGroupDTOs(groupDTOs);
        result.setUserDTOs(organization.getUsers().stream()
            .map(u -> modelMapper.map(u, UserDTO.class))
            .collect(Collectors.toSet()));
        return result;
    }

    private List<GroupRepresentation> getDefaultOrganizationIdGroups(Organization organization) {
        Set<GroupRepresentation> defaultGroups = realmResource.getDefaultGroups().stream()
            .map(g -> realmResource.groups().group(g.getId()).toRepresentation())
            .collect(Collectors.toSet());

        GroupRepresentation groupRoot = new GroupRepresentation();
        groupRoot.setName(organization.getId().toString());
        groupRoot.setPath("/" + organization.getId().toString());

        Response response = realmResource.groups().add(groupRoot);
        GroupResource parentGroup = realmResource.groups()
            .group(CreatedResponseUtil.getCreatedId(response));

        for (GroupRepresentation gr : defaultGroups) {

            String subGroupName = StringUtils.capitalize(gr.getName().replace("console-default-", "")) + " group";
            GroupRepresentation subGroup = new GroupRepresentation();
            subGroup.setName(subGroupName);
            subGroup.setPath("/" + organization.getId() + "/" + subGroupName);
            subGroup.setRealmRoles(gr.getRealmRoles());

            Response subGroupResponse = parentGroup.subGroup(subGroup);

            List<RoleRepresentation> roles = gr.getRealmRoles().stream()
                .map(r -> realmResource.roles().get(r).toRepresentation())
                .collect(Collectors.toList());

            realmResource.groups().group(CreatedResponseUtil.getCreatedId(subGroupResponse))
                .roles().realmLevel().add(roles);
        }

        return parentGroup.toRepresentation().getSubGroups();
    }
}
