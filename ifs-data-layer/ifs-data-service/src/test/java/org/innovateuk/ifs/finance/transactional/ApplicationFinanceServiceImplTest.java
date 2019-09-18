package org.innovateuk.ifs.finance.transactional;


import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.handler.IndustrialCostFinanceHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDelegate;
import org.innovateuk.ifs.finance.mapper.ApplicationFinanceMapper;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRepository;
import org.innovateuk.ifs.finance.repository.ApplicationFinanceRowRepository;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.junit.Assert.*;
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
    public void organisationSeeksFunding() {
        Long competitionId = 1L;
        Long applicationId = 1L;
        Long organisationId = 1L;
        Long projectId = 1L;

        Competition competition = newCompetition().withId(competitionId).build();

        Application application = newApplication().withId(applicationId).withCompetition(competition).build();

        Organisation organisation = newOrganisation().withOrganisationType(newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build()).build();
        when(organisationRepositoryMock.findById(organisationId)).thenReturn(Optional.of(organisation));

        ApplicationFinance applicationFinance = newApplicationFinance().withApplication(application).build();
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(applicationId, organisationId)).thenReturn(applicationFinance);

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withOrganisation(organisationId)
                .withGrantClaimPercentage(20).build();
        when(applicationFinanceMapperMock.mapToResource(applicationFinance)).thenReturn(applicationFinanceResource);

        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(competitionId, organisation.getOrganisationType().getId())).thenReturn(organisationFinanceDefaultHandlerMock);

        when(organisationFinanceDefaultHandlerMock.getOrganisationFinances(applicationFinanceResource.getId())).thenReturn(applicationFinanceResource.getFinanceOrganisationDetails());

        when(applicationFinanceRowRepositoryMock.findByTargetId(applicationFinanceResource.getId())).thenReturn(singletonList(new ApplicationFinanceRow(1L, "", "", "", 20, BigDecimal.ZERO, applicationFinance, null)));

        ServiceResult<Boolean> result = service.organisationSeeksFunding(projectId, applicationId, organisationId);

        assertTrue(result.isSuccess());

        assertTrue(result.getSuccess());
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