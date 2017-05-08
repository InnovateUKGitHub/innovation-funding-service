package org.innovateuk.ifs.finance.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.domain.ApplicationFinanceRow;
import org.innovateuk.ifs.finance.handler.ApplicationFinanceHandler;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaField;
import org.innovateuk.ifs.finance.domain.FinanceRowMetaValue;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceDefaultHandler;
import org.innovateuk.ifs.finance.handler.OrganisationFinanceHandler;
import org.innovateuk.ifs.finance.handler.item.SubContractingCostHandler;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResourceId;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.resource.cost.SubContractingCost;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.OrganisationType;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.LambdaMatcher.lambdaMatches;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceRowBuilder.newApplicationFinanceRow;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaFieldBuilder.newFinanceRowMetaField;
import static org.innovateuk.ifs.finance.builder.FinanceRowMetaValueBuilder.newFinanceRowMetaValue;
import static org.innovateuk.ifs.finance.handler.item.GrantClaimHandler.COST_KEY;
import static org.innovateuk.ifs.finance.handler.item.GrantClaimHandler.GRANT_CLAIM;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationTypeBuilder.newOrganisationType;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 *
 */
public class FinanceRowServiceImplTest extends BaseServiceUnitTest<FinanceRowServiceImpl> {

    @Mock
    private OrganisationFinanceHandler organisationFinanceHandlerMock;

    @Mock
    private OrganisationFinanceDefaultHandler organisationFinanceDefaultHandlerMock;

    @Mock
    private SubContractingCostHandler subContractingCostHandler;

    @Override
    protected FinanceRowServiceImpl supplyServiceUnderTest() {
        return new FinanceRowServiceImpl();
    }

    private FinanceRowItem newFinanceRowItem;
    private ApplicationFinance applicationFinance;
    private Application application;
    private OrganisationType organisationType;
    private long costId;
    private FinanceRowMetaField financeRowMetaField;

    @Before
    public void setUp() {
        costId = 1;
        String metaFieldTitle = "country";
        String metaFieldType = "String";

        application = newApplication()
                .withCompetition(newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build()
                ).build();
        organisationType = newOrganisationType().withOrganisationType(OrganisationTypeEnum.RESEARCH).build();
        newFinanceRowItem = new SubContractingCost(costId, new BigDecimal(10), "Scotland", "nibbles", "purring");
        applicationFinance = newApplicationFinance()
                .withApplication(application)
                .withOrganisation(newOrganisation().withOrganisationType(organisationType).build())
                .build();
        financeRowMetaField = newFinanceRowMetaField()
                .withTitle(metaFieldTitle)
                .withType(metaFieldType).build();

        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);
        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(organisationType.getId())).thenReturn(organisationFinanceDefaultHandlerMock);
    }

    @Mock
    private ApplicationFinanceHandler applicationFinanceHandlerMock;

    @Test
    public void testFindApplicationFinanceByApplicationIdAndOrganisation() {

        Organisation organisation = newOrganisation().build();
        Application application = newApplication().build();

        ApplicationFinance existingFinance = newApplicationFinance().withOrganisationSize(organisation).withApplication(application).build();
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

        ApplicationFinance existingFinance = newApplicationFinance().withOrganisationSize(organisation).withApplication(application).build();
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
        Organisation organisation = newOrganisation().withOrganisationType(newOrganisationType().withOrganisationType(OrganisationTypeEnum.BUSINESS).build()).build();
        final Competition openCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.OPEN).build();
        Application application = newApplication().withCompetition(openCompetition).build();

        when(applicationRepositoryMock.findOne(123L)).thenReturn(application);
        when(organisationRepositoryMock.findOne(456L)).thenReturn(organisation);
        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(OrganisationTypeEnum.BUSINESS.getId())).thenReturn(organisationFinanceDefaultHandlerMock);

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
        final Competition openCompetition = newCompetition().withCompetitionStatus(CompetitionStatus.IN_ASSESSMENT).build();
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

        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(organisation.getOrganisationType().getId())).thenReturn(organisationFinanceDefaultHandlerMock);

        Map<FinanceRowType, FinanceRowCostCategory> costs = new HashMap<>();

        when(organisationFinanceDefaultHandlerMock.getOrganisationFinances(applicationFinanceResource.getId())).thenReturn(costs);

        when(applicationFinanceRowRepositoryMock.findByTargetId(applicationFinanceResource.getId())).thenReturn(asList(new ApplicationFinanceRow(1L, COST_KEY, "", GRANT_CLAIM, 20, BigDecimal.ZERO, applicationFinance,null)));

        ServiceResult<Boolean> result = service.organisationSeeksFunding(projectId, applicationId, organisationId);

        assertTrue(result.isSuccess());

        assertFalse(result.getSuccessObject());
    }

    @Test
    public void testAlreadyExistingMetaValueShouldBeUpdated() {
        List<FinanceRowMetaValue> currentFinanceRowMetaValue = Collections.singletonList(newFinanceRowMetaValue()
                .withFinanceRowMetaField(financeRowMetaField)
                .withValue("England")
                .build());

        List<FinanceRowMetaValue> newFinanceRowMetaValue = Collections.singletonList(newFinanceRowMetaValue()
                .withFinanceRowMetaField(financeRowMetaField)
                .withValue("purring")
                .build());

        ApplicationFinanceRow convertedApplicationFinanceRow = newApplicationFinanceRow()
                .withFinanceRowMetadata(newFinanceRowMetaValue)
                .withTarget(applicationFinance).build();

        ApplicationFinanceRow currentApplicationFinanceRow = newApplicationFinanceRow()
                .withFinanceRowMetadata(currentFinanceRowMetaValue)
                .withTarget(applicationFinance).build();

        when(applicationFinanceRowRepositoryMock.findOne(costId)).thenReturn(currentApplicationFinanceRow);
        when(organisationFinanceDefaultHandlerMock.costItemToCost(any())).thenReturn(convertedApplicationFinanceRow);
        when(applicationFinanceRowRepositoryMock.save(any(ApplicationFinanceRow.class))).thenReturn(convertedApplicationFinanceRow);
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(any(), any())).thenReturn(currentFinanceRowMetaValue.get(0));

        ServiceResult<FinanceRowItem> result = service.updateCost(costId, newFinanceRowItem);

        assertTrue(result.isSuccess());

        FinanceRowMetaValue combinedFinanceRowMetaValue = currentFinanceRowMetaValue.get(0);
        combinedFinanceRowMetaValue.setValue(newFinanceRowMetaValue.get(0).getValue());

        verify(financeRowMetaValueRepositoryMock, times(1)).save(combinedFinanceRowMetaValue);
    }

    @Test
    public void testNonExistingMetaValueShouldBeCreated() {
        List<FinanceRowMetaValue> financeRowMetaValue = Collections.singletonList(
                newFinanceRowMetaValue()
                        .withFinanceRowMetaField(financeRowMetaField)
                        .withValue("England")
                        .build()
        );

        ApplicationFinanceRow convertedApplicationFinanceRow = newApplicationFinanceRow()
                .withFinanceRowMetadata(financeRowMetaValue)
                .withTarget(applicationFinance).build();

        ApplicationFinanceRow currentApplicationFinanceRow = newApplicationFinanceRow()
                .withTarget(applicationFinance).build();

        when(applicationFinanceRowRepositoryMock.findOne(costId)).thenReturn(currentApplicationFinanceRow);
        when(organisationFinanceDefaultHandlerMock.costItemToCost(any())).thenReturn(convertedApplicationFinanceRow);
        when(applicationFinanceRowRepositoryMock.save(any(ApplicationFinanceRow.class))).thenReturn(currentApplicationFinanceRow);
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(any(), any())).thenReturn(null);
        when(financeRowMetaFieldRepositoryMock.findOne(financeRowMetaField.getId())).thenReturn(financeRowMetaField);

        ServiceResult<FinanceRowItem> result = service.updateCost(costId, newFinanceRowItem);

        assertTrue(result.isSuccess());
        verify(financeRowMetaValueRepositoryMock, times(1)).save(financeRowMetaValue.get(0));
    }

    @Test
    public void testNoAttachedMetaValueDoesNotCreateOrUpdateMetaValue() {
        ApplicationFinanceRow convertedApplicationFinanceRow = newApplicationFinanceRow()
                .withTarget(applicationFinance).build();

        ApplicationFinanceRow currentApplicationFinanceRow = newApplicationFinanceRow()
                .withTarget(applicationFinance).build();

        when(applicationFinanceRowRepositoryMock.findOne(costId)).thenReturn(currentApplicationFinanceRow);
        when(organisationFinanceDefaultHandlerMock.costItemToCost(any())).thenReturn(convertedApplicationFinanceRow);
        when(applicationFinanceRowRepositoryMock.save(any(ApplicationFinanceRow.class))).thenReturn(currentApplicationFinanceRow);
        when(financeRowMetaValueRepositoryMock.financeRowIdAndFinanceRowMetaFieldId(any(), any())).thenReturn(null);
        when(financeRowMetaFieldRepositoryMock.findOne(financeRowMetaField.getId())).thenReturn(financeRowMetaField);

        ServiceResult<FinanceRowItem> result = service.updateCost(costId, newFinanceRowItem);

        assertTrue(result.isSuccess());
        verify(financeRowMetaValueRepositoryMock, times(0)).save(any(FinanceRowMetaValue.class));
    }

    @Test
    public void testFindApplicationFinanceDetailsByApplicationId() {

        List<ApplicationFinanceResource> existingFinances = newApplicationFinanceResource().withApplication(1L).build(3);
        when(applicationFinanceHandlerMock.getApplicationFinances(1L)).thenReturn(existingFinances);

        ServiceResult<List<ApplicationFinanceResource>> result = service.financeDetails(1L);
        assertTrue(result.isSuccess());

        assertEquals(existingFinances, result.getSuccessObject());
    }
}
