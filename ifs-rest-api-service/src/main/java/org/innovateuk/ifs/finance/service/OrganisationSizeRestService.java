package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.OrganisationSizeResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link OrganisationSizeResource} related data.
 */
public interface OrganisationSizeRestService {

    RestResult<List<OrganisationSizeResource>> getOrganisationSizes();
}
