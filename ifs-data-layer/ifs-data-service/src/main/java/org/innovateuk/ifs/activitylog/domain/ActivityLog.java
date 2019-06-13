package org.innovateuk.ifs.activitylog.domain;

import org.hibernate.annotations.Immutable;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.user.domain.User;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.ZonedDateTime;

import static javax.persistence.EnumType.STRING;

@Entity
@Immutable
@EntityListeners(AuditingEntityListener.class)
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicationId", referencedColumnName = "id")
    private Application application;

    @Enumerated(STRING)
    private ActivityType type;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="createdBy", referencedColumnName="id", nullable = false, updatable = false)
    private User createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdOn;

    public ActivityLog(Application application, ActivityType type) {
        this.application = application;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public Application getApplication() {
        return application;
    }

    public ActivityType getType() {
        return type;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

}
