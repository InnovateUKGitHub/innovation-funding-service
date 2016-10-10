package com.worth.ifs.user.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.Contract;
import com.worth.ifs.user.resource.ContractResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static com.worth.ifs.user.builder.ContractBuilder.newContract;
import static com.worth.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class ContractServiceImplTest extends BaseServiceUnitTest<ContractServiceImpl> {
    @InjectMocks
    private ContractService contractService = new ContractServiceImpl();

    @Override
    protected ContractServiceImpl supplyServiceUnderTest() {
        final ContractServiceImpl service = new ContractServiceImpl();
        return service;
    }

    @Test
    public void getCurrent() throws Exception {
        Contract contract = newContract().build();
        ContractResource contractResource = newContractResource().build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(contract);
        when(contractMapperMock.mapToResource(same(contract))).thenReturn(contractResource);

        ServiceResult<ContractResource> result = contractService.getCurrent();
        assertTrue(result.isSuccess());
        assertEquals(contractResource, result.getSuccessObject());

        verify(contractRepositoryMock, only()).findByCurrentTrue();
    }

    @Test
    public void getCurrent_notFound() throws Exception {
        Contract contract = newContract().build();
        ContractResource contractResource = newContractResource().build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(null);
        when(contractMapperMock.mapToResource(same(contract))).thenReturn(contractResource);

        ServiceResult<ContractResource> result = contractService.getCurrent();
        assertTrue(result.isFailure());
        assertEquals(result.getErrors().get(0).getErrorKey(), GENERAL_NOT_FOUND.getErrorKey());

        verify(contractRepositoryMock, only()).findByCurrentTrue();
    }

}