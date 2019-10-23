package org.innovateuk.ifs.project.projectteam;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class PendingPartnerProgressRestServiceImpl extends BaseRestService implements PendingPartnerProgressRestService {
    private String pendingPartnerProgressUrl = "/project/%d/organisation/%d/pending-partner-progress";

    @Override
    public RestResult<PendingPartnerProgressResource> getPendingPartnerProgress(long projectId, long organisationId) {
        return getWithRestResult(format(pendingPartnerProgressUrl, projectId, organisationId), PendingPartnerProgressResource.class);
    }

    @Override
    public RestResult<Void> markYourOrganisationComplete(long projectId, long organisationId) {
        return postWithRestResult(format(pendingPartnerProgressUrl, projectId, organisationId) + "/your-organisation-complete");
    }

    @Override
    public RestResult<Void> markYourFundingComplete(long projectId, long organisationId) {
        return postWithRestResult(format(pendingPartnerProgressUrl, projectId, organisationId) + "/your-funding-complete");
    }

    @Override
    public RestResult<Void> markTermsAndConditionsComplete(long projectId, long organisationId) {
        return postWithRestResult(format(pendingPartnerProgressUrl, projectId, organisationId) + "/terms-and-conditions-complete");
    }

    @Override
    public RestResult<Void> markYourOrganisationIncomplete(long projectId, long organisationId) {
        return postWithRestResult(format(pendingPartnerProgressUrl, projectId, organisationId) + "/your-organisation-incomplete");
    }

    @Override
    public RestResult<Void> markYourFundingIncomplete(long projectId, long organisationId) {
        return postWithRestResult(format(pendingPartnerProgressUrl, projectId, organisationId) + "/your-funding-incomplete");
    }

    @Override
    public RestResult<Void> markTermsAndConditionsIncomplete(long projectId, long organisationId) {
        return postWithRestResult(format(pendingPartnerProgressUrl, projectId, organisationId) + "/terms-and-conditions-incomplete");
    }

    @Override
    public RestResult<Void> completePartnerSetup(long projectId, long organisationId) {
        return postWithRestResult(format(pendingPartnerProgressUrl, projectId, organisationId));
    }
}
