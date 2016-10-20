package com.worth.ifs.user.controller;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.ContractResource;
import com.worth.ifs.user.transactional.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This RestController exposes CRUD operations for
 * {@link com.worth.ifs.user.transactional.ContractServiceImpl}
 * to manage {@link com.worth.ifs.user.domain.Contract} related data.
 */

@RestController
@RequestMapping("/contract")
public class ContractController {
    @Autowired
    ContractService contractService;

    @RequestMapping("/findCurrent")
    public RestResult<ContractResource> findAll() {
        return contractService.getCurrent().toGetResponse();
    }
}
