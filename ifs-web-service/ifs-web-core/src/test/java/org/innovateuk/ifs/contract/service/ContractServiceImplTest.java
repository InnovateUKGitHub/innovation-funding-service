package org.innovateuk.ifs.contract.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.innovateuk.ifs.user.service.ContractRestService;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.user.builder.ContractResourceBuilder.newContractResource;
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
