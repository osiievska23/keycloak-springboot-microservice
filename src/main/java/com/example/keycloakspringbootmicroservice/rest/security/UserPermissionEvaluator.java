package com.example.keycloakspringbootmicroservice.rest.security;

import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

public class UserPermissionEvaluator implements PermissionEvaluator {

    private final UserPermissionChecker permissionChecker;

    @Autowired
    public UserPermissionEvaluator(
        UserPermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object domainObject, Object permission) {
        if ((authentication == null) || (domainObject == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(authentication, domainObject.toString(), permission.toString().toUpperCase());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String targetType,
        Object permission) {
        if ((authentication == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(authentication, targetType.toUpperCase(), permission.toString().toUpperCase());
    }

    private boolean hasPrivilege(Authentication authentication, String targetType, String permission) {
        return true;
    }
}
