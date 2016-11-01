package com.worth.ifs.contract.service;

import com.worth.ifs.user.resource.ContractResource;

/**
 * Interface for CRUD operations on {@link com.worth.ifs.user.resource.ContractResource} related data.
 */
public interface ContractService {

    ContractResource getCurrentContract();

}