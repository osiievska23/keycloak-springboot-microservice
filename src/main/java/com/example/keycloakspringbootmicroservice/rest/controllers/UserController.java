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

//    @GetMapping("/anonymous")
//    public ResponseEntity<String> getAnonymous() {
//        log.info("Hello anonymous :)");
//        return ResponseEntity.ok("Hello anonymous :)");
//    }
//
//    @RolesAllowed("service:billing")
//    @GetMapping("/admin")
////    @PreAuthorize("hasRole('service:billing')")
//    public ResponseEntity<String> getAdmin() {
//        log.info("Hello ADMIN :)");
//        return ResponseEntity.ok("Hello ADMIN :)");
//    }
//
//    @GetMapping("/writer")
//    @PreAuthorize("hasRole('user:update')")
//    public ResponseEntity<String> getWriter(@RequestHeader String Authorization) {
//        log.info("Hello WRITER :)");
//        return ResponseEntity.ok("Hello WRITER :)");
//    }
//
//    @GetMapping("/viwer")
//    @PreAuthorize("hasRole('group:view')")
//    public ResponseEntity<String> getViewer(@RequestHeader String Authorization) {
//        log.info("Hello VIEWER :)");
//        return ResponseEntity.ok("Hello VIEWER :)");
//    }
//
//    @GetMapping("/writer-viwer")
//    @PreAuthorize("hasRole({'user:update', 'group:view'})")
//    public ResponseEntity<String> getAllUser(@RequestHeader String Authorization) {
//        log.info("Hello All User");
//        return ResponseEntity.ok("Hello All User");
//    }
}
