package org.innovateuk.ifs.activitylog.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.resource.Role;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;

public class ActivityLogResource {

    private ActivityType activityType;
    private long authoredBy;
    private String authoredByName;
    private Set<Role> authoredByRoles;
    private ZonedDateTime createdOn;

    // Optional
    private Long organisation;
    private String organisationName;
    private Long documentConfig;
    private String documentConfigName;
    private Long query;
    private FinanceChecksSectionType queryType;
    private boolean organisationRemoved;

    ActivityLogResource() {}

    public ActivityLogResource(ActivityType activityType, long authoredBy, String authoredByName, Set<Role> authoredByRoles, ZonedDateTime createdOn, Long organisation, String organisationName, Long documentConfig, String documentConfigName, Long query, FinanceChecksSectionType queryType, boolean organisationRemoved) {
        this.activityType = activityType;
        this.authoredBy = authoredBy;
        this.authoredByName = authoredByName;
        this.authoredByRoles = authoredByRoles;
        this.createdOn = createdOn;
        this.organisation = organisation;
        this.organisationName = organisationName;
        this.documentConfig = documentConfig;
        this.documentConfigName = documentConfigName;
        this.query = query;
        this.queryType = queryType;
        this.organisationRemoved = organisationRemoved;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public long getAuthoredBy() {
        return authoredBy;
    }

    public String getAuthoredByName() {
        return authoredByName;
    }

    public Set<Role> getAuthoredByRoles() {
        return authoredByRoles;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public Long getOrganisation() {
        return organisation;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public Long getDocumentConfig() {
        return documentConfig;
    }

    public String getDocumentConfigName() {
        return documentConfigName;
    }

    public Long getQuery() {
        return query;
    }

    public FinanceChecksSectionType getQueryType() {
        return queryType;
    }

    public boolean isOrganisationRemoved() {
        return organisationRemoved;
    }

    @JsonIgnore
    public boolean isInternalUser() {
        return getAuthoredByRoles().stream().anyMatch(role -> Role.internalRoles().contains(role));
    }

    @JsonIgnore
    public boolean isExternalFinanceUser() {
        return getAuthoredByRoles().stream().anyMatch(role -> Role.EXTERNAL_FINANCE.equals(role));
    }

    @JsonIgnore
    public boolean isIfsAdmin() {
        return getAuthoredByRoles().contains(IFS_ADMINISTRATOR);
    }

}
