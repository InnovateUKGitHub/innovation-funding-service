package com.worth.ifs.user.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.user.domain.Contract;
import com.worth.ifs.user.resource.ContractResource;

/**
 * ContractRestServiceImpl is a utility for CRUD operations on {@link Contract}.
 * This class connects to the {@link com.worth.ifs.user.controller.ContractController}
 * through a REST call.
 */
public class ContractRestServiceImpl extends BaseRestService implements ContractRestService {
    private String contractRestURL = "/contract";

    @Override
    public RestResult<ContractResource> getCurrentContract() {
        return getWithRestResult(contractRestURL + "/findCurrent", ContractResource.class);
    }
}
