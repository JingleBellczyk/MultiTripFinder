package org.dyploma.userinfo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user_info")
public class UserInfo {
    @Id
    private UUID uuid;
    private String firstName;
    private String lastName;
    @Email
    private String email;

    @Builder
    public UserInfo(
            UUID uuid,
            Long version,
            Instant createdDate,
            Instant lastModifiedDate,
            String firstName,
            String lastName,
            String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
