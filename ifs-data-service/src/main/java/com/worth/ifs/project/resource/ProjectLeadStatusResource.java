package com.worth.ifs.project.resource;

import com.worth.ifs.project.constant.ProjectActivityStates;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ProjectLeadStatusResource extends ProjectPartnerStatusResource {
    //Required for Json Mapping.
    public ProjectLeadStatusResource() {}

    public ProjectLeadStatusResource(String name, OrganisationTypeEnum organisationType, ProjectActivityStates projectDetailsStatus, ProjectActivityStates bankDetailsStatus, ProjectActivityStates financeChecksStatus, ProjectActivityStates spendProfileStatus, ProjectActivityStates monitoringOfficerStatus, ProjectActivityStates otherDocumentsStatus, ProjectActivityStates grantOfferLetterStatus) {
        super(name, organisationType, projectDetailsStatus, bankDetailsStatus, financeChecksStatus, spendProfileStatus, monitoringOfficerStatus, otherDocumentsStatus, grantOfferLetterStatus);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .toString();
    }
}