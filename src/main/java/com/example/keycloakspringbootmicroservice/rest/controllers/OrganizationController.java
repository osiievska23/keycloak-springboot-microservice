package com.example.keycloakspringbootmicroservice.rest.controllers;

import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.ORGANIZATION_ID;
import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.ORGANIZATION_ID_PATH;
import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.ORGANIZATION_PATH;
import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.ROOT_PATH;

import com.example.keycloakspringbootmicroservice.dto.OrganizationDTO;
import com.example.keycloakspringbootmicroservice.rest.requests.CreateOrganizationRequest;
import com.example.keycloakspringbootmicroservice.rest.security.jwt.JwtAuthenticationUtils;
import com.example.keycloakspringbootmicroservice.services.OrganizationService;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ROOT_PATH)
public class OrganizationController {

    private final OrganizationService organizationService;
    private final HttpServletRequest httpServletRequest;
    private final JwtAuthenticationUtils authenticationUtils;

    public OrganizationController(OrganizationService organizationService, HttpServletRequest httpServletRequest,
        JwtAuthenticationUtils authenticationUtils) {
        this.organizationService = organizationService;
        this.httpServletRequest = httpServletRequest;
        this.authenticationUtils = authenticationUtils;
    }

    @PostMapping(ORGANIZATION_PATH)
    public OrganizationDTO createOrganization(@Valid @RequestBody CreateOrganizationRequest request) {
        String jwtHeaderFromRequest = authenticationUtils.getJwtHeaderFromRequest(httpServletRequest);
        String ownerEmail = authenticationUtils.getUserEmailFromJwt(jwtHeaderFromRequest);
        return organizationService.createOrganization(request, ownerEmail);
    }

    @PreAuthorize("hasPermission(#organizationId, 'view')")
    @GetMapping(ORGANIZATION_PATH + ORGANIZATION_ID_PATH)
    public OrganizationDTO getOrganizationById(@PathVariable(ORGANIZATION_ID) String organizationId) {
        return organizationService.getOrganizationById(UUID.fromString(organizationId));
    }
}
