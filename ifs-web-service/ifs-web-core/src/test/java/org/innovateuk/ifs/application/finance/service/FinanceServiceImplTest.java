package org.innovateuk.ifs.application.finance.service;

import org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FinanceServiceImplTest {

    @InjectMocks
    private FinanceServiceImpl service;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Test
    public void testGetApplicationFinanceByApplicationIdAndOrganisationId() {

        Long applicationId = 1L;
        Long organisationId = 1L;

        ApplicationFinanceResource applicationFinanceResource = ApplicationFinanceResourceBuilder.newApplicationFinanceResource().build();

        when(applicationFinanceRestService.getApplicationFinance(applicationId, organisationId)).thenReturn(restSuccess(applicationFinanceResource));

        ApplicationFinanceResource result = service.getApplicationFinanceByApplicationIdAndOrganisationId(applicationId, organisationId);

        assertEquals(applicationFinanceResource, result);
    }

    @Test
    public void testGetApplicationFinanceDetailsByApplicationId() {

        Long applicationId = 1L;

        List<ApplicationFinanceResource> applicationFinanceResources = ApplicationFinanceResourceBuilder.newApplicationFinanceResource().build(3);

        when(applicationFinanceRestService.getFinanceDetails(applicationId)).thenReturn(restSuccess(applicationFinanceResources));

        List<ApplicationFinanceResource> result = service.getApplicationFinanceDetails(applicationId);

        assertEquals(applicationFinanceResources, result);
    }
}
