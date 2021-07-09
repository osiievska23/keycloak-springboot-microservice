package com.example.keycloakspringbootmicroservice.mapper;

import com.example.keycloakspringbootmicroservice.domain.Account;
import com.example.keycloakspringbootmicroservice.dto.AccountDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    default AccountDTO accountToAccountDto(Account account) {
//        return AccountDTO.builder()
//            .id(account.getId())
//            .name(account.getName())
//            .ownerId(account.getOwner())
//            .registrationDateTime(account.getRegistrationDateTime())
//            .build();
        return AccountDTO.builder()
            .id(account.getId())
            .name(account.getName())
            .registrationDateTime(account.getRegistrationDateTime())
            .build();
    }
}
