package org.dyploma.infrastructure.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Version
    private Long version;

    private Instant createdDate;
    private Instant lastModifiedDate;

    @Override
    public String toString() {
        return "uuid="
                + uuid
                + ", version="
                + version
                + ", createDate="
                + createdDate
                + ", lastModifiedDate="
                + lastModifiedDate
                + ",";
    }
}
