package com.example.keycloakspringbootmicroservice.domain.enums;

public enum Status {

    ACTIVE("1"), INACTIVE("2"), DELETED("3");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public static String getStatusValue(Status status) {
        return status.value;
    }
}
