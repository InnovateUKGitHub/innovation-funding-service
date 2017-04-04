package org.innovateuk.ifs.project.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.finance.resource.*;
import org.springframework.stereotype.Service;

/**
 * Rest Service for dealing with Project finance operations
 */
@Service
public class FinanceCheckRestServiceImpl extends BaseRestService implements FinanceCheckRestService {

    @Override
    public RestResult<FinanceCheckResource> getByProjectAndOrganisation(Long projectId, Long organisationId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.ORGANISATION_PATH + "/" + organisationId + FinanceCheckURIs.PATH;
        return getWithRestResult(url, FinanceCheckResource.class);
    }

    @Override
    public RestResult<Void> update(FinanceCheckResource financeCheckResource) {
        String url = FinanceCheckURIs.BASE_URL + FinanceCheckURIs.PATH;
        return postWithRestResult(url, financeCheckResource, Void.class);
    }

    @Override
    public RestResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.PATH;
        return getWithRestResult(url, FinanceCheckSummaryResource.class);
    }

    @Override
    public RestResult<FinanceCheckOverviewResource> getFinanceCheckOverview(Long projectId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.PATH + "/overview";
        return getWithRestResult(url, FinanceCheckOverviewResource.class);
    }

    @Override
    public RestResult<Void> approveFinanceCheck(Long projectId, Long organisationId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.ORGANISATION_PATH + "/" + organisationId + FinanceCheckURIs.PATH + "/approve";
        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<FinanceCheckEligibilityResource> getFinanceCheckEligibilityDetails(Long projectId, Long organisationId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.ORGANISATION_PATH + "/" + organisationId + FinanceCheckURIs.PATH + "/eligibility";
        return getWithRestResult(url, FinanceCheckEligibilityResource.class);
    }
}
