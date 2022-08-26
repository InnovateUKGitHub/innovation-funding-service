package org.innovateuk.ifs.project.eligibility.propulator;

import com.google.common.collect.ImmutableMap;
import org.innovateuk.ifs.application.finance.viewmodel.CostChangeViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesProjectFinancesViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.eligibility.populator.ProjectFinanceChangesViewModelPopulator;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class KtpProjectFinanceChangesViewModelPopulatorTest {

    private final FundingType fundingType;

    @InjectMocks
    private ProjectFinanceChangesViewModelPopulator populator;

    @Mock
    private FinanceCheckRestService financeCheckRestService;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ProjectProcurementMilestoneRestService projectProcurementMilestoneRestService;

    @Mock
    private ApplicationProcurementMilestoneRestService applicationProcurementMilestoneRestService;

    @Mock
    private ProcessRoleRestService processRoleRestService;

    private long projectId = 2L;
    private long applicationId = 4L;
    private long organisationId = 3L;
    private long competitionId = 6L;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public KtpProjectFinanceChangesViewModelPopulatorTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        FinanceCheckEligibilityResource financeCheckEligibilityResource = newFinanceCheckEligibilityResource().build();
        when(financeCheckRestService.getFinanceCheckEligibilityDetails(projectId, organisationId)).thenReturn(restSuccess(financeCheckEligibilityResource));

        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = new HashMap<>();
        financeOrganisationDetails.put(FinanceRowType.LABOUR, new LabourCostCategory());

        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withFinanceOrganisationDetails(financeOrganisationDetails).build();
        when(projectFinanceRestService.getProjectFinance(projectId, organisationId)).thenReturn(restSuccess(projectFinanceResource));

        ProcessRoleResource processRoleResource = newProcessRoleResource().withOrganisation(organisationId).build();
        when(processRoleRestService.findProcessRole(applicationId)).thenReturn(restSuccess(asList(processRoleResource)));

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource().withFinanceOrganisationDetails(financeOrganisationDetails).build();
        when(applicationFinanceRestService.getFinanceDetails(applicationId, organisationId)).thenReturn(restSuccess(applicationFinanceResource));

        CompetitionResource competitionResource = newCompetitionResource().withProcurementMilestones(true).build();
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));
    }

    @Test
    public void shouldVerifyNonFECCostRowEntriesDisplay() {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(fundingType)
                .build();
        OrganisationResource organisationResource = newOrganisationResource().withId(organisationId).build();

        Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = ImmutableMap.of(
                FinanceRowType.OTHER_COSTS, newDefaultCostCategory().build(),
                FinanceRowType.ASSOCIATE_SALARY_COSTS, newDefaultCostCategory().build(),
                FinanceRowType.ASSOCIATE_DEVELOPMENT_COSTS, newDefaultCostCategory().build(),
                FinanceRowType.CONSUMABLES, newDefaultCostCategory().build(),
                FinanceRowType.KTP_TRAVEL, newDefaultCostCategory().build(),
                FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT, newDefaultCostCategory().build(),
                FinanceRowType.INDIRECT_COSTS, newDefaultCostCategory().build());

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource()
                .withFecEnabled(false)
                .withOrganisation(organisationId)
                .withFinanceOrganisationDetails(financeOrganisationDetails)
                .build();
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().withFinanceOrganisationDetails(financeOrganisationDetails).build();
        ProjectFinanceChangesProjectFinancesViewModel projectFinanceChangesProjectFinancesViewModel =
                populator.getProjectFinancesViewModel(competitionResource,organisationResource,applicationFinanceResource,projectFinanceResource);
        List<CostChangeViewModel> costChangeViewModelsList = projectFinanceChangesProjectFinancesViewModel.getEntries();
        assertThat(costChangeViewModelsList).hasSize(7);
        boolean isIndirectCostRowPresent = false;
        boolean isAcademicAndSecreterialSupport = false;
        for(CostChangeViewModel  costChangeViewModel :costChangeViewModelsList) {
            if (costChangeViewModel.getSection().equals(FinanceRowType.INDIRECT_COSTS.getDisplayName())) {
                isIndirectCostRowPresent = true;
            }
            if (costChangeViewModel.getSection().equals(FinanceRowType.ACADEMIC_AND_SECRETARIAL_SUPPORT.getDisplayName())) {
                isAcademicAndSecreterialSupport = true;
            }
        }
        assertThat(isIndirectCostRowPresent).isTrue();
        assertThat(isAcademicAndSecreterialSupport).isTrue();
    }
}
