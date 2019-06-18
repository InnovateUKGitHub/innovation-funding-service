package org.innovateuk.ifs.activitylog.domain;

import org.hibernate.annotations.Immutable;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.threads.domain.Query;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisationId", referencedColumnName = "id")
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentConfigId", referencedColumnName = "id")
    private CompetitionDocument competitionDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "threadId", referencedColumnName = "id")
    private Query query;

    private ActivityLog() {
    }

    public ActivityLog(Application application, ActivityType type, CompetitionDocument competitionDocument) {
        this.application = application;
        this.type = type;
        this.competitionDocument = competitionDocument;
    }

    public ActivityLog(Application application, ActivityType type, Query query, Organisation organisation) {
        this.application = application;
        this.type = type;
        this.query = query;
        this.organisation = organisation;
    }

    public ActivityLog(Application application, ActivityType type) {
        this.application = application;
        this.type = type;
    }

    public ActivityLog(Application application, ActivityType type, Organisation organisation) {
        this.application = application;
        this.type = type;
        this.organisation = organisation;
    }

    public Long getId() {
        return id;
    }

    public Application getApplication() {
        return application;
    }

    public Organisation getOrganisation() {
        return organisation;
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

    public CompetitionDocument getCompetitionDocument() {
        return competitionDocument;
    }

    public Query getQuery() {
        return query;
    }
}
