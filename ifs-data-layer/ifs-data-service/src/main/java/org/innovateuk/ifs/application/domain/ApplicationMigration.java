package org.innovateuk.ifs.application.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * ApplicationMigration defines database relations and a model to use in server side.
 */
@Entity
public class ApplicationMigration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long applicationId;

    @Enumerated(EnumType.STRING)
    private MigrationStatus status;

    private ZonedDateTime createdOn;

    private ZonedDateTime updatedOn;

    public ApplicationMigration() {
    }

    public ApplicationMigration(Long applicationId, MigrationStatus status) {
        this.applicationId = applicationId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public MigrationStatus getStatus() {
        return status;
    }

    public void setStatus(MigrationStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public ZonedDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(ZonedDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }
}
