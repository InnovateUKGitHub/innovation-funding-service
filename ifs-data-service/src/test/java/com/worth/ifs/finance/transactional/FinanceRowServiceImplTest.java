package com.worth.ifs.finance.transactional;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.commons.error.CommonFailureKeys;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.FinanceRow;
import com.worth.ifs.finance.handler.OrganisationFinanceDefaultHandler;
import com.worth.ifs.finance.handler.OrganisationFinanceDelegate;
import com.worth.ifs.finance.handler.OrganisationFinanceHandler;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResourceId;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.OrganisationType;
import com.worth.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.LambdaMatcher.lambdaMatches;
import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static com.worth.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static com.worth.ifs.finance.handler.item.GrantClaimHandler.COST_KEY;
import static com.worth.ifs.finance.handler.item.GrantClaimHandler.GRANT_CLAIM;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

/**
 *
 */
public class FinanceRowServiceImplTest extends BaseServiceUnitTest<FinanceRowServiceImpl> {

    @Mock
    private OrganisationFinanceHandler organisationFinanceHandlerMock;

    @Mock
    private OrganisationFinanceDelegate organisationFinanceDelegateMock;

    @Mock
    private OrganisationFinanceDefaultHandler organisationFinanceDefaultHandlerMock;

    @Override
    protected FinanceRowServiceImpl supplyServiceUnderTest() {
        return new FinanceRowServiceImpl();
    }

    @Test
    public void testFindApplicationFinanceByApplicationIdAndOrganisation() {

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
        assertEquals(expectedFinance, result.getSuccessObject());
    }

    @Test
    public void testFindApplicationFinanceByApplicationId() {

        Organisation organisation = newOrganisation().build();
        Application application = newApplication().build();

        ApplicationFinance existingFinance = newApplicationFinance().withOrganisation(organisation).withApplication(application).build();
        when(applicationFinanceRepositoryMock.findByApplicationId(123L)).thenReturn(singletonList(existingFinance));

        ApplicationFinanceResource expectedFinance = newApplicationFinanceResource().
                with(id(existingFinance.getId())).
                withOrganisation(organisation.getId()).
                withApplication(application.getId()).
                build();

        when(applicationFinanceRepositoryMock.findByApplicationId(123L)).thenReturn(singletonList(existingFinance));
        when(applicationFinanceMapperMock.mapToResource(existingFinance)).thenReturn(expectedFinance);

        ServiceResult<List<ApplicationFinanceResource>> result = service.findApplicationFinanceByApplication(123L);
        assertTrue(result.isSuccess());

        assertEquals(singletonList(expectedFinance), result.getSuccessObject());
    }

    @Test
    public void testAddCost() {
        Organisation organisation = newOrganisation().withOrganisationType(new OrganisationType("Business", null)).build();
        final Competition openCompetition = newCompetition().withCompetitionStatus(CompetitionResource.Status.OPEN).build();
        Application application = newApplication().withCompetition(openCompetition).build();

        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);
        when(organisationRepositoryMock.findOne(456L)).thenReturn(organisation);
        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler("Business")).thenReturn(organisationFinanceDefaultHandlerMock);

        ApplicationFinance newFinance = new ApplicationFinance(application, organisation);

        ApplicationFinance newFinanceExpectations = argThat(lambdaMatches(finance -> {
            assertEquals(application, finance.getApplication());
            assertEquals(organisation, finance.getOrganisation());
            return true;
        }));


        ApplicationFinanceResource expectedFinance = newApplicationFinanceResource().
                with(id(newFinance.getId())).
                withOrganisation(organisation.getId()).
                withApplication(application.getId()).
                build();

        when(applicationFinanceRepositoryMock.save(newFinanceExpectations)).thenReturn(newFinance);
        when(applicationFinanceMapperMock.mapToResource(newFinance)).thenReturn(expectedFinance);

        ServiceResult<ApplicationFinanceResource> result = service.addCost(new ApplicationFinanceResourceId(123L, 456L));
        assertTrue(result.isSuccess());
        assertEquals(expectedFinance, result.getSuccessObject());
    }

    @Test
    public void testAddWhenApplicationNotOpen() {
        final Competition openCompetition = newCompetition().withCompetitionStatus(CompetitionResource.Status.IN_ASSESSMENT).build();
        Application application = newApplication().withCompetition(openCompetition).build();
        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);
        ServiceResult<ApplicationFinanceResource> result = service.addCost(new ApplicationFinanceResourceId(123L, 456L));
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.COMPETITION_NOT_OPEN));
    }

    @Test
    public void testOrganisationSeeksFunding(){
        Long applicationId = 1L;
        Long organisationId = 1L;
        Long projectId = 1L;

        Organisation organisation = newOrganisation().withOrganisationType(newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build()).build();
        when(organisationRepositoryMock.findOne(organisationId)).thenReturn(organisation);

        ApplicationFinance applicationFinance = newApplicationFinance().build();
        when(applicationFinanceRepositoryMock.findByApplicationIdAndOrganisationId(applicationId, organisationId)).thenReturn(applicationFinance);

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource().withOrganisation(organisationId).withGrantClaimPercentage(20).build();
        when(applicationFinanceMapperMock.mapToResource(applicationFinance)).thenReturn(applicationFinanceResource);

        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(organisation.getOrganisationType().getName())).thenReturn(organisationFinanceDefaultHandlerMock);

        Map<FinanceRowType, FinanceRowCostCategory> costs = new HashMap<>();

        when(organisationFinanceDefaultHandlerMock.getOrganisationFinances(applicationFinanceResource.getId())).thenReturn(costs);

        when(financeRowRepositoryMock.findByApplicationFinanceId(applicationFinanceResource.getId())).thenReturn(asList(new FinanceRow(1L, COST_KEY, "", GRANT_CLAIM, 20, BigDecimal.ZERO, applicationFinance,null)));

        ServiceResult<Boolean> result = service.organisationSeeksFunding(projectId, applicationId, organisationId);

        assertTrue(result.isSuccess());

        assertFalse(result.getSuccessObject());
    }
}
