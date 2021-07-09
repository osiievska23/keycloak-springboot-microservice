package com.example.keycloakspringbootmicroservice.rest.controllers;

import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.ACCOUNTS_PATH;
import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.ACCOUNT_ID_PATH;
import static com.example.keycloakspringbootmicroservice.constants.ApplicationConstants.ROOT_PATH;

import com.example.keycloakspringbootmicroservice.dto.AccountDTO;
import com.example.keycloakspringbootmicroservice.rest.requests.CreateAccountRequest;
import com.example.keycloakspringbootmicroservice.rest.security.jwt.JwtAuthenticationUtils;
import com.example.keycloakspringbootmicroservice.services.AccountService;
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
public class AccountController {

    private final AccountService accountService;
    private final HttpServletRequest httpServletRequest;
    private final JwtAuthenticationUtils authenticationUtils;

    public AccountController(AccountService accountService, HttpServletRequest httpServletRequest,
        JwtAuthenticationUtils authenticationUtils) {
        this.accountService = accountService;
        this.httpServletRequest = httpServletRequest;
        this.authenticationUtils = authenticationUtils;
    }

    @PostMapping(ACCOUNTS_PATH)
    public AccountDTO createAccount(@Valid @RequestBody CreateAccountRequest request) {
        String jwtHeaderFromRequest = authenticationUtils.getJwtHeaderFromRequest(httpServletRequest);
        String ownerEmail = authenticationUtils.getUserEmailFromJwt(jwtHeaderFromRequest);
        return accountService.createAccount(request, ownerEmail);
    }

    // todo rename roles
    @PreAuthorize("hasPermission('console_user_manage', #accountId)  && isAuthenticated()")
    @GetMapping(ACCOUNTS_PATH + ACCOUNT_ID_PATH)
    public AccountDTO getAccountById(@PathVariable("accountId") String accountId) {
        return accountService.getAccountById(UUID.fromString(accountId));
    }
}
