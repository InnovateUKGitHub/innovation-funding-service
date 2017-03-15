package org.innovateuk.ifs.project.financecheck;

import org.innovateuk.ifs.project.finance.resource.FinanceCheckResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
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
}
