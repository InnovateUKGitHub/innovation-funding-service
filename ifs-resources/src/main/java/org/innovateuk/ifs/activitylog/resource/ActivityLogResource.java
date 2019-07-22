package org.innovateuk.ifs.activitylog.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.resource.Role;

import java.time.ZonedDateTime;
import java.util.Set;

import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;

public class ActivityLogResource {

    private ActivityType activityType;
    private Long createdBy;
    private String createdByName;
    private Set<Role> createdByRoles;
    private ZonedDateTime createdOn;

    // Optional
    private Long organisation;
    private String organisationName;
    private Long documentConfig;
    private String documentConfigName;
    private Long query;
    private FinanceChecksSectionType queryType;

    ActivityLogResource() {}

    public ActivityLogResource(ActivityType activityType, Long createdBy, String createdByName, Set<Role> createdByRoles, ZonedDateTime createdOn, Long organisation, String organisationName, Long documentConfig, String documentConfigName, Long query, FinanceChecksSectionType queryType) {
        this.activityType = activityType;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.createdByRoles = createdByRoles;
        this.createdOn = createdOn;
        this.organisation = organisation;
        this.organisationName = organisationName;
        this.documentConfig = documentConfig;
        this.documentConfigName = documentConfigName;
        this.query = query;
        this.queryType = queryType;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public Set<Role> getCreatedByRoles() {
        return createdByRoles;
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

    @JsonIgnore
    public boolean isInternalUser() {
        return getCreatedByRoles().stream().anyMatch(role -> Role.internalRoles().contains(role));
    }

    @JsonIgnore
    public boolean isIfsAdmin() {
        return getCreatedByRoles().contains(IFS_ADMINISTRATOR);
    }

}
