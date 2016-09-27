package com.worth.ifs.project.finance.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.resource.SpendProfileResource;
import com.worth.ifs.project.resource.SpendProfileTableResource;

/**
 * Rest Service for dealing with Project finance operations
 */
public interface FinanceCheckRestService {

    RestResult<FinanceCheckResource> getByProjectAndOrganisation(Long projectId, Long organisationId);

    RestResult<FinanceCheckResource> update(FinanceCheckResource financeCheckResource);

}
