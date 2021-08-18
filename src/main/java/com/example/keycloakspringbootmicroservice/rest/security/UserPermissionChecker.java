package com.example.keycloakspringbootmicroservice.rest.security;

import com.example.keycloakspringbootmicroservice.config.InstanceFactory;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserPermissionChecker {

    private final RealmResource realmResource;

    public UserPermissionChecker(Environment environment, InstanceFactory instanceFactory) {
        this.realmResource = instanceFactory.getKeycloakInstance().realm(environment.getProperty("keycloak.realm"));
    }

    public boolean hasUserPrivilege(Authentication authentication, String organizationId, String permission) {
        String keycloakUserId = authentication.getPrincipal().toString();
        return realmResource.users().get(keycloakUserId).groups().stream()
            .map(g -> realmResource.groups().group(g.getId()))
            .filter(g -> g.toRepresentation().getPath().contains(organizationId))
            .flatMap(g -> g.toRepresentation().getRealmRoles().stream())
            .anyMatch(permission::contains);
    }
}
