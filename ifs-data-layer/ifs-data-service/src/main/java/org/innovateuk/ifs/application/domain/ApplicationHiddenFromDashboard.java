package org.innovateuk.ifs.application.domain;

import org.hibernate.annotations.Immutable;
import org.innovateuk.ifs.user.domain.ProcessRole;
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
public class ApplicationHiddenFromDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="createdBy", referencedColumnName="id")
    private User createdBy;

    @CreatedDate
    private ZonedDateTime createdOn;

    @JoinColumn(name = "process_role_id")
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private ProcessRole processRole;

    public ApplicationHiddenFromDashboard(ProcessRole processRole) {
        this.processRole = processRole;
    }

    public Long getId() {
        return id;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public ProcessRole getProcessRole() {
        return processRole;
    }
}