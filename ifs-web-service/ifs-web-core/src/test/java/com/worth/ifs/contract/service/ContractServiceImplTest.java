package com.worth.ifs.contract.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.user.resource.ContractResource;
import com.worth.ifs.user.service.ContractRestService;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ContractServiceImplTest extends BaseServiceUnitTest<ContractService> {

    @Mock
    private ContractRestService contractRestService;

    @Override
    protected ContractService supplyServiceUnderTest() {
        return new ContractServiceImpl();
    }

    @Test
    public void getCurrentContract() throws Exception {
        ContractResource expected = newContractResource().build();

        when(contractRestService.getCurrentContract()).thenReturn(RestResult.restSuccess(expected));

        ContractResource response = service.getCurrentContract();

        assertEquals(expected, response);
        verify(contractRestService, only()).getCurrentContract();
    }
}