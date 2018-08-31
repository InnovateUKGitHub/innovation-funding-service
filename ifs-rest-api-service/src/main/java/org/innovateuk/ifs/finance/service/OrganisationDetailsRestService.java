package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;

/**
 * Interface for CRUD operations on {@link org.innovateuk.ifs.project.finance.resource.FinanceCheckResource} related data.
 */
public interface OrganisationDetailsRestService {

    RestResult<Long> getTurnover(Long applicationId, Long organisationId);

    RestResult<Long> getHeadCount(Long applicationId, Long organisationId);
}
