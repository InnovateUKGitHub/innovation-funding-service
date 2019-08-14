package org.innovateuk.ifs.activitylog.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Immutable;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
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
import java.util.Optional;

import static java.util.Optional.ofNullable;
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

    public Optional<Organisation> getOrganisation() {
        return ofNullable(organisation);
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

    public Optional<CompetitionDocument> getCompetitionDocument() {
        return ofNullable(competitionDocument);
    }

    public Optional<Query> getQuery() {
        return ofNullable(query);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ActivityLog that = (ActivityLog) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(application, that.application)
                .append(type, that.type)
                .append(createdBy, that.createdBy)
                .append(createdOn, that.createdOn)
                .append(organisation, that.organisation)
                .append(competitionDocument, that.competitionDocument)
                .append(query, that.query)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(application)
                .append(type)
                .append(createdBy)
                .append(createdOn)
                .append(organisation)
                .append(competitionDocument)
                .append(query)
                .toHashCode();
    }
}
