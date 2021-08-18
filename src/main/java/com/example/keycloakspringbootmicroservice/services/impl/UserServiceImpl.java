package com.example.keycloakspringbootmicroservice.services.impl;

import static com.example.keycloakspringbootmicroservice.constants.ExceptionConstants.USER_ALREADY_EXISTS_EXCEPTION;

import com.example.keycloakspringbootmicroservice.domain.User;
import com.example.keycloakspringbootmicroservice.domain.enums.Status;
import com.example.keycloakspringbootmicroservice.dto.UserDTO;
import com.example.keycloakspringbootmicroservice.exceptions.ForbiddenException;
import com.example.keycloakspringbootmicroservice.rest.repositories.UserRepository;
import com.example.keycloakspringbootmicroservice.services.UserService;
import java.util.Collections;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final RealmResource realmResource;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserServiceImpl(RealmResource realmResource,
        UserRepository userRepository, ModelMapper modelMapper) {
        this.realmResource = realmResource;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException(USER_ALREADY_EXISTS_EXCEPTION);
        }

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userDTO.getPassword());
        credential.setTemporary(false);

        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setUsername(userDTO.getUsername());
        keycloakUser.setEmail(userDTO.getEmail());
        keycloakUser.setCredentials(Collections.singletonList(credential));
        keycloakUser.setEnabled(true);

        Response response = realmResource.users().create(keycloakUser);
        if (response.getStatus() != 201) {
            throw new ForbiddenException(response.getStatusInfo().getReasonPhrase());
        }

        String keycloakUserId = CreatedResponseUtil.getCreatedId(response);
        UserResource u = realmResource.users().get(keycloakUserId);
        u.sendVerifyEmail(keycloakUserId);

        User user = User.builder()
            .email(userDTO.getEmail())
            .status(Status.ACTIVE)
            .keycloakUserId(keycloakUserId)
            .build();

        return modelMapper.map(userRepository.save(user), UserDTO.class);
    }
}
