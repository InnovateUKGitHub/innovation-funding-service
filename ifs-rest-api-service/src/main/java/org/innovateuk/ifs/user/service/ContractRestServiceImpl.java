package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.springframework.stereotype.Service;

/**
 * ContractRestServiceImpl is a utility for CRUD operations on {@link ContractResource}.
 * This class connects to the { org.innovateuk.ifs.user.controller.ContractController}
 * through a REST call.
 */
@Service
public class ContractRestServiceImpl extends BaseRestService implements ContractRestService {
    private String contractRestURL = "/contract";

    @Override
    public RestResult<ContractResource> getCurrentContract() {
        return getWithRestResult(contractRestURL + "/findCurrent", ContractResource.class);
    }
}
