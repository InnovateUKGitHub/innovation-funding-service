package com.worth.ifs.project.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;
import com.worth.ifs.project.finance.workflow.financechecks.resource.FinanceCheckProcessResource;

/**
 * Rest Service for dealing with Project finance operations
 */
public interface FinanceCheckRestService {

    RestResult<FinanceCheckResource> getByProjectAndOrganisation(Long projectId, Long organisationId);

    RestResult<Void> update(FinanceCheckResource financeCheckResource);

    RestResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId);

    RestResult<Void> approveFinanceCheck(Long projectId, Long organisationId);

    RestResult<FinanceCheckProcessResource> getFinanceCheckApprovalStatus(Long projectId, Long organisationId);
}
