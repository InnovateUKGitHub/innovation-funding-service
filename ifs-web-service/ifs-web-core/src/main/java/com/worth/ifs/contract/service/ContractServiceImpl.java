package com.worth.ifs.contract.service;

import com.worth.ifs.user.resource.ContractResource;
import com.worth.ifs.user.service.ContractRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link ContractResource} related data,
 * through the RestService {@link com.worth.ifs.user.service.ContractRestService}.
 */
@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRestService contractRestService;

    @Override
    public ContractResource getCurrentContract() {
        return contractRestService.getCurrentContract().getSuccessObjectOrThrowException();
    }
}