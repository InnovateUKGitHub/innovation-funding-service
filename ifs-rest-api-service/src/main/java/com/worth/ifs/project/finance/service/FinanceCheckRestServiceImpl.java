package com.worth.ifs.project.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;
import com.worth.ifs.project.finance.resource.FinanceCheckURIs;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;
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
    public RestResult<Void> approveFinanceCheck(Long projectId, Long organisationId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.ORGANISATION_PATH + "/" + organisationId + FinanceCheckURIs.PATH + "/approve";
        return postWithRestResult(url, Void.class);
    }

    @Override
    public RestResult<FinanceCheckProcessResource> getFinanceCheckApprovalStatus(Long projectId, Long organisationId) {
        String url = FinanceCheckURIs.BASE_URL + "/" + projectId + FinanceCheckURIs.ORGANISATION_PATH + "/" + organisationId + FinanceCheckURIs.PATH + "/status";
        return getWithRestResult(url, FinanceCheckProcessResource.class);
    }
}
