package com.worth.ifs.user.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.builder.ContractBuilder;
import com.worth.ifs.user.domain.Contract;
import com.worth.ifs.user.resource.ContractResource;
import org.junit.Test;

import static com.worth.ifs.commons.service.ParameterizedTypeReferences.affiliationResourceListType;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.OK;

public class ContractRestServiceImplTest extends BaseRestServiceUnitTest<ContractRestServiceImpl> {
    protected ContractRestServiceImpl registerRestServiceUnderTest() {
        return new ContractRestServiceImpl();
    }

    private static final String contractUrl = "/contract";

    @Test
    public void getCurrentContract() {
        ContractResource contractResource = ContractResourceBuilder.newContractResource().build();

        setupGetWithRestResultExpectations(contractUrl + "/findCurrent", ContractResource.class, contractResource, OK);
        final RestResult<ContractResource> result = service.getCurrentContract();
        assertTrue(result.isSuccess());
    }
}