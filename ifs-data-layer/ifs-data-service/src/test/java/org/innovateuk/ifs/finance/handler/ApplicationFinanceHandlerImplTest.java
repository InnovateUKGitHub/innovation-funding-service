package org.innovateuk.ifs.finance.handler;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.finance.domain.ApplicationFinance;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceBuilder.newApplicationFinance;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.MaterialsCostBuilder.newMaterials;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

public class ApplicationFinanceHandlerImplTest extends BaseUnitTestMocksTest  {

    @InjectMocks
    private ApplicationFinanceHandler handler = new ApplicationFinanceHandlerImpl();

    @Mock
    private OrganisationFinanceDefaultHandler organisationFinanceDefaultHandlerMock;

    private long applicationId = 3L;
    private Map<FinanceRowType, FinanceRowCostCategory> researchOrgFinances;
    private Map<FinanceRowType, FinanceRowCostCategory> businessOrgFinances;

    @Before
    public void setup() throws Exception {
        long researchOrgId = 2L;
        long businessOrgId = 4L;

        Organisation researchOrg = newOrganisation().withId(researchOrgId).withOrganisationType(OrganisationTypeEnum.RESEARCH).build();
        Organisation businessOrg = newOrganisation().withId(businessOrgId).withOrganisationType(OrganisationTypeEnum.BUSINESS).build();
        Competition competition = newCompetition().build();
        Application application = newApplication().withCompetition(competition).build();

        researchOrgFinances = asMap(
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                        newMaterials().
                                withCost(new BigDecimal("1000"), new BigDecimal("10001")).
                                withQuantity(1, 2).
                                build(2)).
                        build());
        researchOrgFinances.forEach((type, category) -> category.calculateTotal());

        businessOrgFinances = asMap(
                FinanceRowType.MATERIALS, newDefaultCostCategory().withCosts(
                        newMaterials().
                                withCost(new BigDecimal("1000"), new BigDecimal("10000")).
                                withQuantity(1, 2).
                                build(2)).
                        build());
        businessOrgFinances.forEach((type, category) -> category.calculateTotal());

        ApplicationFinance appFinanceResearchOrg = newApplicationFinance()
                .withApplication(application)
                .withOrganisation(researchOrg)
                .build();

        ApplicationFinance appFinanceBusinessOrg = newApplicationFinance()
                .withApplication(application)
                .withOrganisation(businessOrg)
                .build();

        when(organisationRepositoryMock.findOne(researchOrgId)).thenReturn(researchOrg);
        when(organisationRepositoryMock.findOne(businessOrgId)).thenReturn(businessOrg);

        when(applicationFinanceRepositoryMock.findByApplicationId(applicationId)).thenReturn(asList(appFinanceResearchOrg, appFinanceBusinessOrg));

        ApplicationFinanceResource appFinanceResourceResearchOrg = newApplicationFinanceResource()
                .withApplication(applicationId)
                .withOrganisation(researchOrgId)
                .build();
        ApplicationFinanceResource appFinanceResourceBusinessOrg = newApplicationFinanceResource()
                .withApplication(applicationId)
                .withOrganisation(businessOrgId)
                .build();
        when(applicationFinanceMapperMock.mapToResource(appFinanceResearchOrg)).thenReturn(appFinanceResourceResearchOrg);
        when(applicationFinanceMapperMock.mapToResource(appFinanceBusinessOrg)).thenReturn(appFinanceResourceBusinessOrg);

        when(organisationFinanceDelegateMock.getOrganisationFinanceHandler(anyLong())).thenReturn(organisationFinanceDefaultHandlerMock);
        when(organisationFinanceDefaultHandlerMock.getOrganisationFinanceTotals(appFinanceResourceResearchOrg.getId(), competition)).thenReturn(researchOrgFinances);
        when(organisationFinanceDefaultHandlerMock.getOrganisationFinanceTotals(appFinanceResourceBusinessOrg.getId(), competition)).thenReturn(businessOrgFinances);
    }

    @Test
    public void getResearchParticipationPercentage_roundedUp() throws Exception {
        BigDecimal expectedRawPercentage = new BigDecimal("50.002400");
        BigDecimal expectedPercentage = new BigDecimal("50.01");

        ApplicationFinanceResource researchFinanceResource = newApplicationFinanceResource().build();
        researchFinanceResource.setFinanceOrganisationDetails(researchOrgFinances);
        BigDecimal researchTotalCost = researchFinanceResource.getTotal();

        ApplicationFinanceResource businessFinanceResource = newApplicationFinanceResource().build();
        businessFinanceResource.setFinanceOrganisationDetails(businessOrgFinances);
        BigDecimal businessTotalCost = businessFinanceResource.getTotal();

        BigDecimal totalCost = researchTotalCost.add(businessTotalCost);
        BigDecimal rawResearchPercentage =  researchTotalCost
                .divide(totalCost, 6, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        assertEquals(expectedRawPercentage, rawResearchPercentage);

        BigDecimal result = handler.getResearchParticipationPercentage(applicationId);
        assertEquals(expectedPercentage, result);
    }
}
