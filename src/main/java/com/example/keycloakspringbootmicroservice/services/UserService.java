package com.example.keycloakspringbootmicroservice.services;

import com.example.keycloakspringbootmicroservice.dto.UserDTO;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);
}
