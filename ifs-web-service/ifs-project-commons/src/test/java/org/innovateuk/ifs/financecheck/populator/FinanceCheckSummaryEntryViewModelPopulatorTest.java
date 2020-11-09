package org.innovateuk.ifs.financecheck.populator;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.financecheck.FinanceCheckService;
import org.innovateuk.ifs.financecheck.viewmodel.FinanceCheckSummaryEntryViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FinanceCheckSummaryEntryViewModelPopulatorTest {

    @InjectMocks
    private FinanceCheckSummaryEntryViewModelPopulator populator;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private FinanceCheckService financeCheckService;

    @Test
    public void shouldPopulateForNonKtp() {
        CompetitionResource competition = newCompetitionResource().build();
        ProjectResource project = newProjectResource().withId(4L).build();
        FinanceCheckEligibilityResource eligibilityOverview = newFinanceCheckEligibilityResource()
                .withContributionToProject(new BigDecimal("5.00"))
                .withTotalCost(new BigDecimal("10.00"))
                .build();
        OrganisationResource organisation = newOrganisationResource().withId(5L).build();
        boolean lead = false;

        FinanceCheckSummaryEntryViewModel result = populator.populate(competition, project, eligibilityOverview, organisation, lead);

        assertEquals(new BigDecimal("5.00"), result.getContributionToProject());
        assertEquals(new BigDecimal("50.0"), result.getPercentageContributionToProject());
        verifyZeroInteractions(partnerOrganisationRestService, financeCheckService);
    }

    @Test
    public void shouldPopulateForKtpLead() {
        CompetitionResource competition = newCompetitionResource().withFundingType(FundingType.KTP).build();
        ProjectResource project = newProjectResource().withId(4L).build();
        FinanceCheckEligibilityResource eligibilityOverview = newFinanceCheckEligibilityResource()
                .withContributionToProject(new BigDecimal("5.00"))
                .withTotalCost(new BigDecimal("10.00"))
                .build();
        OrganisationResource organisation = newOrganisationResource().withId(5L).build();
        boolean lead = true;

        List<PartnerOrganisationResource> partnerOrgs = asList(
                newPartnerOrganisationResource().withOrganisation(organisation.getId()).build(),
                newPartnerOrganisationResource().withOrganisation(6L).build()
        );
        given(partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId())).willReturn(restSuccess(partnerOrgs));

        FinanceCheckEligibilityResource eligibilityDetails = newFinanceCheckEligibilityResource()
                .withContributionToProject(new BigDecimal("0.00"))
                .withTotalCost(new BigDecimal("10.00"))
                .build();
        given(financeCheckService.getFinanceCheckEligibilityDetails(project.getId(), 6L)).willReturn(eligibilityDetails);

        FinanceCheckSummaryEntryViewModel result = populator.populate(competition, project, eligibilityOverview, organisation, lead);

        assertEquals(new BigDecimal("0.00"), result.getContributionToProject());
        assertEquals(new BigDecimal("0.0"), result.getPercentageContributionToProject());
    }

    @Test
    public void shouldPopulateForKtpNonLead() {
        CompetitionResource competition = newCompetitionResource().withFundingType(FundingType.KTP).build();
        ProjectResource project = newProjectResource().withId(4L).build();
        FinanceCheckEligibilityResource eligibilityOverview = newFinanceCheckEligibilityResource()
                .withContributionToProject(new BigDecimal("0.00"))
                .withTotalCost(new BigDecimal("0.00"))
                .build();
        OrganisationResource organisation = newOrganisationResource().withId(5L).build();
        boolean lead = false;

        List<PartnerOrganisationResource> partnerOrgs = asList(
                newPartnerOrganisationResource().withOrganisation(organisation.getId()).build(),
                newPartnerOrganisationResource().withOrganisation(6L).build()
        );
        given(partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId())).willReturn(restSuccess(partnerOrgs));

        FinanceCheckEligibilityResource eligibilityDetails = newFinanceCheckEligibilityResource()
                .withContributionToProject(new BigDecimal("5.00"))
                .withTotalCost(new BigDecimal("10.00"))
                .build();
        given(financeCheckService.getFinanceCheckEligibilityDetails(project.getId(), 6L)).willReturn(eligibilityDetails);

        FinanceCheckSummaryEntryViewModel result = populator.populate(competition, project, eligibilityOverview, organisation, lead);

        assertEquals(new BigDecimal("5.00"), result.getContributionToProject());
        assertEquals(new BigDecimal("50.0"), result.getPercentageContributionToProject());
    }
}
