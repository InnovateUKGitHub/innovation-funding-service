package com.worth.ifs.application.finance.service;

import com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
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
}
