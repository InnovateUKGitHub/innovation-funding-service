package org.innovateuk.ifs.activitylog.resource;

import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.resource.Role;

import java.time.ZonedDateTime;
import java.util.Set;

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
    private String documenConfigName;
    private Long queryId;
    private FinanceChecksSectionType queryType;

    private ActivityLogResource() {}

    public ActivityLogResource(ActivityType activityType, Long createdBy, String createdByName, Set<Role> createdByRoles, ZonedDateTime createdOn, Long organisation, String organisationName, Long documentConfig, String documenConfigName, Long queryId, FinanceChecksSectionType queryType) {
        this.activityType = activityType;
        this.createdBy = createdBy;
        this.createdByName = createdByName;
        this.createdByRoles = createdByRoles;
        this.createdOn = createdOn;
        this.organisation = organisation;
        this.organisationName = organisationName;
        this.documentConfig = documentConfig;
        this.documenConfigName = documenConfigName;
        this.queryId = queryId;
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

    public String getDocumenConfigName() {
        return documenConfigName;
    }

    public Long getQueryId() {
        return queryId;
    }

    public FinanceChecksSectionType getQueryType() {
        return queryType;
    }
}
