package com.worth.ifs.project.resource;

import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.user.resource.OrganisationTypeEnum;

/**
 * Used for returning status for project lead.
 * Constructor for this type of user includes monitoring officer, other documents and grant offer letter statuses.
 */
public class ProjectLeadStatusResource extends ProjectPartnerStatusResource {
    //Required for Json Mapping.
    ProjectLeadStatusResource() {}

    public ProjectLeadStatusResource(
            Long organisationId,
            String name,
            OrganisationTypeEnum organisationType,
            ProjectActivityStates projectDetailsStatus,
            ProjectActivityStates monitoringOfficerStatus,
            ProjectActivityStates bankDetailsStatus,
            ProjectActivityStates financeChecksStatus,
            ProjectActivityStates spendProfileStatus,
            ProjectActivityStates otherDocumentsStatus,
            ProjectActivityStates grantOfferLetterStatus,
            ProjectActivityStates financeContactStatus) {
        super(organisationId, name, organisationType, projectDetailsStatus, monitoringOfficerStatus, bankDetailsStatus, financeChecksStatus, spendProfileStatus, otherDocumentsStatus, grantOfferLetterStatus, financeContactStatus);
    }
}