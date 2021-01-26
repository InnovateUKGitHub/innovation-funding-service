package org.innovateuk.ifs.project.projectteam;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;

public interface PendingPartnerProgressRestService {

    RestResult<PendingPartnerProgressResource> getPendingPartnerProgress(long projectId, long organisationId);
    RestResult<Void> markYourOrganisationComplete(long projectId, long organisationId);
    RestResult<Void> markYourFundingComplete(long projectId, long organisationId);
    RestResult<Void> markTermsAndConditionsComplete(long projectId, long organisationId);
    RestResult<Void> markYourOrganisationIncomplete(long projectId, long organisationId);
    RestResult<Void> markYourFundingIncomplete(long projectId, long organisationId);
    RestResult<Void> markTermsAndConditionsIncomplete(long projectId, long organisationId);
    RestResult<Void> completePartnerSetup(long projectId, long organisationId);

}
