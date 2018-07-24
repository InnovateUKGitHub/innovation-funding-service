package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;

/**
 * Interface for CRUD operations on {@link OrganisationSizeResource} related data.
 */
public interface OrganisationDetailsRestService {

    RestResult<Long> getTurnover(Long applicationId, Long organisationId);

    RestResult<Long> getHeadCount(Long applicationId, Long organisationId);
}
