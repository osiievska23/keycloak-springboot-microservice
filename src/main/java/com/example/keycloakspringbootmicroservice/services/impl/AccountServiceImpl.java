package com.example.keycloakspringbootmicroservice.services.impl;

import static com.example.keycloakspringbootmicroservice.constants.ExceptionConstants.CREDENTIALS_INVALID_EXCEPTION;

import com.example.keycloakspringbootmicroservice.domain.Account;
import com.example.keycloakspringbootmicroservice.domain.Group;
import com.example.keycloakspringbootmicroservice.domain.User;
import com.example.keycloakspringbootmicroservice.dto.AccountDTO;
import com.example.keycloakspringbootmicroservice.dto.GroupDTO;
import com.example.keycloakspringbootmicroservice.dto.UserDTO;
import com.example.keycloakspringbootmicroservice.mapper.ApplicationMapper;
import com.example.keycloakspringbootmicroservice.rest.repositories.AccountRepository;
import com.example.keycloakspringbootmicroservice.rest.repositories.GroupRepository;
import com.example.keycloakspringbootmicroservice.rest.repositories.UserRepository;
import com.example.keycloakspringbootmicroservice.rest.requests.CreateAccountRequest;
import com.example.keycloakspringbootmicroservice.services.AccountService;
import java.time.Instant;
import java.util.HashSet;
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
public class AccountServiceImpl implements AccountService {

    private final RealmResource realmResource;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ApplicationMapper applicationMapper;
    private final ModelMapper modelMapper;

    public AccountServiceImpl(RealmResource realmResource,
        AccountRepository accountRepository,
        UserRepository userRepository, GroupRepository groupRepository,
        ApplicationMapper applicationMapper, ModelMapper modelMapper) {
        this.realmResource = realmResource;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.applicationMapper = applicationMapper;
        this.modelMapper = modelMapper;
    }

    @Override
    public AccountDTO createAccount(CreateAccountRequest request, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
            .orElseThrow(() -> new BadRequestException(CREDENTIALS_INVALID_EXCEPTION));

        Account account = Account.builder()
            .name(request.getName())
            .owner(owner.getId())
            .registrationDateTime(Instant.now())
            .users(new HashSet<>())
            .build();
        accountRepository.save(account);

        List<GroupRepresentation> defaultAccountGroups = getDefaultAccountGroups(account);
        for (GroupRepresentation gr : defaultAccountGroups) {
            Group group = Group.builder()
                .account(account)
                .name(gr.getName())
                .keycloakGroupId(gr.getId())
                .users(new HashSet<>())
                .build();

            groupRepository.save(group);
        }

        return applicationMapper.accountToAccountDto(account);
    }

    @Override
    public AccountDTO getAccountById(UUID accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new NotFoundException(CREDENTIALS_INVALID_EXCEPTION));

        AccountDTO result = modelMapper.map(account, AccountDTO.class);
//        Set<GroupDTO> groupDTOs = groupRepository.findAllByAccountId(account.getId()).stream()
//            .map(g -> modelMapper.map(g, GroupDTO.class))
//            .collect(Collectors.toSet());
//        result.setGroupDTOs(groupDTOs);
//        result.setUserDTOs(account.getUsers().stream()
//            .map(u -> modelMapper.map(u, UserDTO.class))
//            .collect(Collectors.toSet()));
        return result;
    }

    private List<GroupRepresentation> getDefaultAccountGroups(Account account) {
        Set<GroupRepresentation> defaultGroups = realmResource.getDefaultGroups().stream()
            .map(g -> realmResource.groups().group(g.getId()).toRepresentation())
            .collect(Collectors.toSet());

        GroupRepresentation accountGroupRoot = new GroupRepresentation();
        accountGroupRoot.setName(account.getId().toString());
        accountGroupRoot.setPath("/" + account.getId().toString());

        Response response = realmResource.groups().add(accountGroupRoot);
        GroupResource parentGroup = realmResource.groups()
            .group(CreatedResponseUtil.getCreatedId(response));

        for (GroupRepresentation gr : defaultGroups) {

            String subGroupName = StringUtils.capitalize(gr.getName().replace("console-default-", "")) + " group";
            GroupRepresentation subGroup = new GroupRepresentation();
            subGroup.setName(subGroupName);
            subGroup.setPath("/" + account.getId() + "/" + subGroupName);
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
