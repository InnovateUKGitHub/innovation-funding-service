package com.worth.ifs.project.financecheck;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.finance.service.FinanceCheckRestService;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FinanceCheckServiceImplTest {
    @InjectMocks
    private FinanceCheckServiceImpl service;

    @Mock
    private FinanceCheckRestService financeCheckRestServiceMock;

    @Test
    public void testGet(){
        Long projectId = 123L;
        Long organisationId = 456L;
        ProjectOrganisationCompositeId key = new ProjectOrganisationCompositeId(projectId, organisationId);

        FinanceCheckResource financeCheckResource = newFinanceCheckResource().build();

        when(financeCheckRestServiceMock.getByProjectAndOrganisation(projectId, organisationId)).thenReturn(restSuccess(financeCheckResource));

        FinanceCheckResource result = service.getByProjectAndOrganisation(key);

        assertEquals(financeCheckResource, result);
    }

    @Test
    public void testUpdate(){
        FinanceCheckResource financeCheckResource = newFinanceCheckResource().build();

        when(financeCheckRestServiceMock.update(financeCheckResource)).thenReturn(restSuccess());

        ServiceResult result = service.update(financeCheckResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void testApprove() {

        FinanceCheckResource financeCheckResource = newFinanceCheckResource().build();

        when(financeCheckRestServiceMock.update(financeCheckResource)).thenReturn(restSuccess());

        ServiceResult result = service.update(financeCheckResource);

        assertTrue(result.isSuccess());

        verify(financeCheckRestServiceMock).update(financeCheckResource);
    }
}
