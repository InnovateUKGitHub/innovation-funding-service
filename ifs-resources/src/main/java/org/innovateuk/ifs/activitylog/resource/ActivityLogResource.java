package org.innovateuk.ifs.activitylog.resource;

import org.innovateuk.ifs.user.resource.Role;

import java.time.ZonedDateTime;
import java.util.Set;

public class ActivityLogResource {

    private ActivityType activityType;
    private String createdByName;
    private Set<Role> createdByRoles;
    private ZonedDateTime createdOn;

    // Optional
    private Long organisation;
    private String organisationName;
    private String documentName;
    private Long queryId;

    private ActivityLogResource() {}

    public ActivityLogResource(ActivityType activityType, String createdByName, Set<Role> createdByRoles, ZonedDateTime createdOn, Long organisation, String organisationName, String documentName, Long queryId) {
        this.activityType = activityType;
        this.createdByName = createdByName;
        this.createdByRoles = createdByRoles;
        this.createdOn = createdOn;
        this.organisation = organisation;
        this.organisationName = organisationName;
        this.documentName = documentName;
        this.queryId = queryId;
    }

    public ActivityType getActivityType() {
        return activityType;
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

    public String getDocumentName() {
        return documentName;
    }

    public Long getQueryId() {
        return queryId;
    }
}
