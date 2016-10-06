package com.worth.ifs.project.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.resource.FinanceCheckSummaryResource;

/**
 * Rest Service for dealing with Project finance operations
 */
public interface FinanceCheckRestService {

    RestResult<FinanceCheckResource> getByProjectAndOrganisation(Long projectId, Long organisationId);

    RestResult<Void> update(FinanceCheckResource financeCheckResource);

    RestResult<FinanceCheckSummaryResource> getFinanceCheckSummary(Long projectId);
}
