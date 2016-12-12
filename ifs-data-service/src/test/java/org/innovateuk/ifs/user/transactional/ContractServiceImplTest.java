package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.Contract;
import org.innovateuk.ifs.user.resource.ContractResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.user.builder.ContractBuilder.newContract;
import static org.innovateuk.ifs.user.builder.ContractResourceBuilder.newContractResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class ContractServiceImplTest extends BaseServiceUnitTest<ContractServiceImpl> {
    @InjectMocks
    private ContractService contractService = new ContractServiceImpl();

    @Override
    protected ContractServiceImpl supplyServiceUnderTest() {
        return new ContractServiceImpl();
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
        verify(contractMapperMock, only()).mapToResource(same(contract));
    }

    @Test
    public void getCurrent_notFound() throws Exception {
        Contract contract = newContract().build();
        ContractResource contractResource = newContractResource().build();

        when(contractRepositoryMock.findByCurrentTrue()).thenReturn(null);
        when(contractMapperMock.mapToResource(same(contract))).thenReturn(contractResource);

        ServiceResult<ContractResource> result = contractService.getCurrent();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Contract.class)));

        verify(contractRepositoryMock, only()).findByCurrentTrue();
        verifyZeroInteractions(contractMapperMock);
    }

}
