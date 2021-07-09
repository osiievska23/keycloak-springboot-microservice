package com.example.keycloakspringbootmicroservice.rest.repositories;

import com.example.keycloakspringbootmicroservice.domain.Account;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    boolean existsByName(String name);

}
