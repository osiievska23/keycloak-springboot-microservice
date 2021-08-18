package com.example.keycloakspringbootmicroservice.rest.controllers;

import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.ROOT_PATH;
import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.USERS_PATH;

import com.example.keycloakspringbootmicroservice.dto.UserDTO;
import com.example.keycloakspringbootmicroservice.rest.requests.CreateUserRequest;
import com.example.keycloakspringbootmicroservice.services.UserService;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ROOT_PATH)
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(USERS_PATH)
    public UserDTO createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(modelMapper.map(request, UserDTO.class));
    }
}
