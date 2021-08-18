package com.example.keycloakspringbootmicroservice.domain;

import com.example.keycloakspringbootmicroservice.domain.enums.Status;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "status!=3")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private Status status;

    @ManyToMany(mappedBy = "users")
    private Set<Organization> organizations;

    @ManyToMany(mappedBy = "users")
    private Set<Group> groups;

    @Column(name = "last_login_time")
    private Instant lastLoginTime;

    @Column(name = "keycloak_user_id")
    private String keycloakUserId;

}
