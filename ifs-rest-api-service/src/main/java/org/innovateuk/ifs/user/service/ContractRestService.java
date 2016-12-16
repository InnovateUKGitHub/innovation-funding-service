package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ContractResource;

/**
 * Interface for CRUD operations on {@link ContractResource} related data.
 */
public interface ContractRestService {
    RestResult<ContractResource> getCurrentContract();
}
