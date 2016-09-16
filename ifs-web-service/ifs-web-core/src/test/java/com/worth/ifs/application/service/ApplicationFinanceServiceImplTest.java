package com.worth.ifs.application.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.service.ApplicationFinanceRestService;
import org.junit.Test;
import org.mockito.Mock;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ApplicationFinanceServiceImplTest extends BaseServiceUnitTest<ApplicationFinanceService> {

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Override
    protected ApplicationFinanceService supplyServiceUnderTest() {
        return new ApplicationFinanceServiceImpl();
    }

    @Test
    public void getApplicationOrganisationFinances() throws Exception {

        Long applicationId = 1L;
        Long organisationId = 1L;

        ApplicationFinanceResource applicationFinanceResource = ApplicationFinanceResourceBuilder.newApplicationFinanceResource()
                .withGrantClaimPercentage(20)
                .build();

        when(applicationFinanceRestService.getApplicationOrganisationFinances(applicationId, organisationId)).thenReturn(restSuccess(applicationFinanceResource));

        ApplicationFinanceResource actualApplicationFinanceResource = service.getApplicationOrganisationFinances(applicationId, organisationId);

        assertEquals(actualApplicationFinanceResource, applicationFinanceResource);
    }
}
