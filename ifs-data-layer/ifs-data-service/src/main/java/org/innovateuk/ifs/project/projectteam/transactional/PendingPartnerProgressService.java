package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;

public interface PendingPartnerProgressService {

    ServiceResult<PendingPartnerProgressResource> getPendingPartnerProgress(long projectId, long organisationId);
    ServiceResult<Void> markYourOrganisationComplete(long projectId, long organisationId);
    ServiceResult<Void> markYourFundingComplete(long projectId, long organisationId);
    ServiceResult<Void> markTermsAndConditionsComplete(long projectId, long organisationId);
    ServiceResult<Void> markYourOrganisationIncomplete(long projectId, long organisationId);
    ServiceResult<Void> markYourFundingIncomplete(long projectId, long organisationId);
    ServiceResult<Void> markTermsAndConditionsIncomplete(long projectId, long organisationId);
    ServiceResult<Void> completePartnerSetup(long projectId, long organisationId);

}
