package org.innovateuk.ifs.glustermigration.domain;

import javax.persistence.*;
import java.util.Objects;

/**
 * Represents a File on the filesystem that can be referenced in the application.
 */
@Entity
public class GlusterMigrationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private String status;

    private String errorMessage;

    public GlusterMigrationStatus() {
    	// no-arg constructor
    }

    public GlusterMigrationStatus(Long id, String status, String errorMessage) {
        this.id = id;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GlusterMigrationStatus that = (GlusterMigrationStatus) o;
        return id.equals(that.id) && status.equals(that.status) && errorMessage.equals(that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, errorMessage);
    }
}
