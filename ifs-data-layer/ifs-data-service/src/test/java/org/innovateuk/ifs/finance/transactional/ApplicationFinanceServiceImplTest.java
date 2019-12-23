package org.innovateuk.ifs.finance.transactional;


import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.handler.IndustrialCostFinanceHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ApplicationFinanceServiceImplTest extends BaseServiceUnitTest<ApplicationFinanceServiceImpl> {

    @Override
    protected ApplicationFinanceServiceImpl supplyServiceUnderTest() {
        return new ApplicationFinanceServiceImpl();
    }

    @Mock
    private ApplicationFinanceHandler applicationFinanceHandlerMock;

    @Mock
    private IndustrialCostFinanceHandler organisationFinanceDefaultHandlerMock;

    @Mock
    private ApplicationFinanceRepository applicationFinanceRepositoryMock;

    @Mock
    private ApplicationFinanceMapper applicationFinanceMapperMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private OrganisationFinanceDelegate organisationFinanceDelegateMock;

    @Mock
    private ApplicationFinanceRowRepository applicationFinanceRowRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;
    @Test
    public void findApplicationFinanceByApplicationIdAndOrganisation() {

        Organisation organisation = newOrganisation().build();
        Application application = newApplication().build();

        ApplicationFinance existingFinance = newApplicationFinance().withOrganisation(organisation).withApplication(application).build();
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(123L, 456L)).thenReturn(existingFinance);

        ApplicationFinanceResource expectedFinance = newApplicationFinanceResource().
                with(id(existingFinance.getId())).
                withOrganisation(organisation.getId()).
                withApplication(application.getId()).
                build();

        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(123L, 456L)).thenReturn(existingFinance);
        when(applicationFinanceMapperMock.mapToResource(existingFinance)).thenReturn(expectedFinance);

        ServiceResult<ApplicationFinanceResource> result = service.findApplicationFinanceByApplicationIdAndOrganisation(123L, 456L);
        assertTrue(result.isSuccess());
        assertEquals(expectedFinance, result.getSuccess());
    }

    @Test
    public void findApplicationFinanceByApplicationId() {

        Organisation organisation = newOrganisation().build();
        Application application = newApplication().build();

        ApplicationFinance existingFinance = newApplicationFinance().withOrganisation(organisation).withApplication(application).build();
        when(applicationFinanceRepositoryMock.findByApplicationId(123L)).thenReturn(Collections.singletonList(existingFinance));

        ApplicationFinanceResource expectedFinance = newApplicationFinanceResource().
                with(id(existingFinance.getId())).
                withOrganisation(organisation.getId()).
                withApplication(application.getId()).
                build();

        when(applicationFinanceRepositoryMock.findByApplicationId(123L)).thenReturn(singletonList(existingFinance));
        when(applicationFinanceMapperMock.mapToResource(existingFinance)).thenReturn(expectedFinance);

        ServiceResult<List<ApplicationFinanceResource>> result = service.findApplicationFinanceByApplication(123L);
        assertTrue(result.isSuccess());

        assertEquals(singletonList(expectedFinance), result.getSuccess());
    }

    @Test
    public void findApplicationFinanceDetailsByApplicationId() {

        List<ApplicationFinanceResource> existingFinances = newApplicationFinanceResource().withApplication(1L).build(3);
        when(applicationFinanceHandlerMock.getApplicationFinances(1L)).thenReturn(existingFinances);

        ServiceResult<List<ApplicationFinanceResource>> result = service.financeDetails(1L);
        assertTrue(result.isSuccess());

        assertEquals(existingFinances, result.getSuccess());
    }

}