package com.worth.ifs.project.resource;

import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Used for returning status for project lead.
 * Constructor for this type of user includes monitoring officer, other documents and grant offer letter statuses.
 */
public class ProjectLeadStatusResource extends ProjectPartnerStatusResource {
    //Required for Json Mapping.
    public ProjectLeadStatusResource() {}

    public ProjectLeadStatusResource(
            String name,
            OrganisationTypeEnum organisationType,
            ProjectActivityStates projectDetailsStatus,
            ProjectActivityStates monitoringOfficerStatus,
            ProjectActivityStates bankDetailsStatus,
            ProjectActivityStates financeChecksStatus,
            ProjectActivityStates spendProfileStatus,
            ProjectActivityStates otherDocumentsStatus,
            ProjectActivityStates grantOfferLetterStatus) {
        super(name, organisationType, projectDetailsStatus, monitoringOfficerStatus, bankDetailsStatus, financeChecksStatus, spendProfileStatus, otherDocumentsStatus, grantOfferLetterStatus);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .toString();
    }
}