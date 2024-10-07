package org.dyploma.userinfo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dyploma.infrastructure.model.AbstractEntity;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Table(name = "user_info")
public class UserInfo extends AbstractEntity {
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
        super(uuid, version, createdDate, lastModifiedDate);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
