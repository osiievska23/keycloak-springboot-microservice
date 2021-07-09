package com.example.keycloakspringbootmicroservice.dto;

import com.example.keycloakspringbootmicroservice.domain.enums.Status;
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
public class UserDTO {

    private String id;
    private String username;
    private String email;
    private String password;
    private Status status;

}
