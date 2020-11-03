package org.innovateuk.ifs.project.core.domain;

import org.innovateuk.ifs.application.domain.Application;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Represents a request to send information about a live project to the external live project system.
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ProjectToBeCreated {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name="applicationId", referencedColumnName="id")
    private Application application;

    private String emailBody;

    @CreatedDate
    private ZonedDateTime created;
    @LastModifiedDate
    private ZonedDateTime lastModified;
    private boolean pending;
    private String message;

    ProjectToBeCreated() {}

    public ProjectToBeCreated(Application application, String emailBody) {
        this.emailBody = emailBody;
        this.application = application;
        this.pending = true;
    }

    public boolean isPending() {
        return pending;
    }

    public String getMessage() {
        return message;
    }

    public Long getId() {
        return id;
    }

    public Application getApplication() {
        return application;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
