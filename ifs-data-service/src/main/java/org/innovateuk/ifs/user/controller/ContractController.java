package org.innovateuk.ifs.user.controller;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.innovateuk.ifs.user.transactional.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This RestController exposes CRUD operations for
 * {@link org.innovateuk.ifs.user.transactional.ContractServiceImpl}
 * to manage {@link org.innovateuk.ifs.user.domain.Contract} related data.
 */

@RestController
@RequestMapping("/contract")
public class ContractController {
    @Autowired
    ContractService contractService;

    @RequestMapping("/findCurrent")
    public RestResult<ContractResource> findCurrent() {
        return contractService.getCurrent().toGetResponse();
    }
}
