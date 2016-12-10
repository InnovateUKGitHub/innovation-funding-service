package org.innovateuk.ifs.contract.service;

import org.innovateuk.ifs.user.resource.ContractResource;
import org.innovateuk.ifs.user.service.ContractRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class contains methods to retrieve and store {@link ContractResource} related data,
 * through the RestService {@link org.innovateuk.ifs.user.service.ContractRestService}.
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
