package com.example.keycloakspringbootmicroservice.config;

import com.example.keycloakspringbootmicroservice.rest.security.UserPermissionChecker;
import com.example.keycloakspringbootmicroservice.rest.security.UserPermissionEvaluator;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final UserPermissionChecker userPermissionChecker;

    public MethodSecurityConfig(UserPermissionChecker userPermissionChecker) {
        this.userPermissionChecker = userPermissionChecker;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        UserPermissionEvaluator userPermissionEvaluator = new UserPermissionEvaluator(userPermissionChecker);
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(userPermissionEvaluator);
        return expressionHandler;
    }
}
