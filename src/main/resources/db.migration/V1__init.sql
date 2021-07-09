CREATE SCHEMA IF NOT EXISTS schema;

CREATE TABLE IF NOT EXISTS schema.accounts
(
    id                     uuid      NOT NULL,
    name                   VARCHAR(255),
    owner_id               uuid      NOT NULL,
    registration_date_time timestamp NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS schema.users
(
    id               uuid NOT NULL,
    email            VARCHAR(255),
    last_login_time  timestamp,
    status           INT,
    keycloak_user_id VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS schema.groups
(
    id                uuid NOT NULL,
    name              VARCHAR(255),
    keycloak_group_id VARCHAR(255),
    account_id        uuid REFERENCES schema.accounts (id),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS schema.groups_has_users
(
    user_id  uuid NOT NULL REFERENCES schema.users (id),
    group_id uuid NOT NULL REFERENCES schema.groups (id),
    PRIMARY KEY (user_id, group_id)
);

CREATE TABLE IF NOT EXISTS schema.account_users
(
    account_id uuid NOT NULL REFERENCES schema.accounts (id),
    user_id    uuid NOT NULL REFERENCES schema.users (id),
    PRIMARY KEY (account_id, user_id)
);
