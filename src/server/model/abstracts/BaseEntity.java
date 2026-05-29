package server.model.abstracts;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseEntity {
    protected String id;
    protected LocalDateTime createdAt;

    public BaseEntity() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public abstract String describeYourself();

    public String getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
