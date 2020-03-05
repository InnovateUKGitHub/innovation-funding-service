package org.innovateuk.ifs.application.domain;

import org.hibernate.annotations.Immutable;
import org.innovateuk.ifs.user.domain.User;
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

    @CreatedDate
    private ZonedDateTime createdOn;

    @JoinColumn(name = "application_id", referencedColumnName="id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

    @JoinColumn(name = "user_id", referencedColumnName="id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public ApplicationHiddenFromDashboard(Application application, User user) {
        this.application = application;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public Application getApplication() {
        return application;
    }

    public User getUser() {
        return user;
    }
}