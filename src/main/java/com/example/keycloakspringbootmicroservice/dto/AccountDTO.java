package com.example.keycloakspringbootmicroservice.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {

    private UUID id;
    private String name;
//    private UUID ownerId;
    private Instant registrationDateTime;
//    private Set<UserDTO> userDTOs;
//    private Set<GroupDTO> groupDTOs;
}
