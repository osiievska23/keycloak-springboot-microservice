package com.example.keycloakspringbootmicroservice.services;

import com.example.keycloakspringbootmicroservice.dto.AccountDTO;
import com.example.keycloakspringbootmicroservice.rest.requests.CreateAccountRequest;
import java.util.UUID;

public interface AccountService {

    AccountDTO createAccount(CreateAccountRequest request, String ownerEmail);

    AccountDTO getAccountById(UUID accountId);
}
