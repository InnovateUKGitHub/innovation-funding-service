package org.innovateuk.ifs.application.domain;

import org.hibernate.annotations.Immutable;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * Entity representing a deleted application.
 */
@Entity
@Immutable
@EntityListeners(AuditingEntityListener.class)
public class DeletedApplicationAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long applicationId;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="deletedBy", referencedColumnName="id")
    private User deletedBy;

    @CreatedDate
    private ZonedDateTime deletedOn;

    public DeletedApplicationAudit(long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getId() {
        return id;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public User getDeletedBy() {
        return deletedBy;
    }

    public ZonedDateTime getDeletedOn() {
        return deletedOn;
    }
}