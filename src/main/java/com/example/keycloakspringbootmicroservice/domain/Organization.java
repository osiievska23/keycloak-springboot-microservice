package com.example.keycloakspringbootmicroservice.domain;

import com.sun.istack.NotNull;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "status!=3")
public class Organization {

    @Id
    @GeneratedValue
    private UUID id;

    @NotEmpty
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "owner_id")
    private UUID owner;

    @NotNull
    @Column(name = "registration_date_time")
    private Instant registrationDateTime;

    @ManyToMany
    @JoinTable(name = "organization_users",
        joinColumns = @JoinColumn(name = "organization_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;

}
