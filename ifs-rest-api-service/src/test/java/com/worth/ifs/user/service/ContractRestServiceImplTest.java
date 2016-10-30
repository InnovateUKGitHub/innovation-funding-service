package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.ContractResource;
import org.junit.Test;


import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ContractRestServiceImplTest extends BaseRestServiceUnitTest<ContractRestServiceImpl> {

    protected ContractRestServiceImpl registerRestServiceUnderTest() {
        return new ContractRestServiceImpl();
    }

    private static final String contractUrl = "/contract";

    @Test
    public void getCurrentContract() {
        ContractResource contractResource = new ContractResource();

        setupGetWithRestResultExpectations(contractUrl + "/findCurrent", ContractResource.class, contractResource, OK);
        final RestResult<ContractResource> result = service.getCurrentContract();
        assertTrue(result.isSuccess());
    }
}