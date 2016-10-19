package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.domain.Contract;
import com.worth.ifs.user.resource.ContractResource;

/**
 * Interface for CRUD operations on {@link Contract} related data.
 */
public interface ContractRestService {
    RestResult<ContractResource> getCurrentContract();
}
