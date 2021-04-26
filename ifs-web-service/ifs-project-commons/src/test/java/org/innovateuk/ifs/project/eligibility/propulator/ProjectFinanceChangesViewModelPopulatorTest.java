package org.innovateuk.ifs.project.eligibility.propulator;

import org.innovateuk.ifs.application.finance.viewmodel.CostChangeViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.MilestoneChangeViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesProjectFinancesViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.resource.category.FinanceRowCostCategory;
import org.innovateuk.ifs.finance.resource.category.LabourCostCategory;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.eligibility.populator.ProjectFinanceChangesViewModelPopulator;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.DefaultCostCategoryBuilder.newDefaultCostCategory;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneResourceBuilder.newApplicationProcurementMilestoneResource;
import static org.innovateuk.ifs.procurement.milestone.builder.ProjectProcurementMilestoneResourceBuilder.newProjectProcurementMilestoneResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectFinanceChangesViewModelPopulatorTest {

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

    @Before
    public void setUp() {
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
    public void shouldDetermineNoChangesInMilestones() {

        List<ApplicationProcurementMilestoneResource> applicationMilestones = asList(
                newApplicationProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newApplicationProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build()
        );

        List<ProjectProcurementMilestoneResource> projectMilestones = asList(
                newProjectProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newProjectProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build()
        );

        when(applicationProcurementMilestoneRestService.getByApplicationIdAndOrganisationId(applicationId, organisationId)).thenReturn(restSuccess(applicationMilestones));
        when(projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(restSuccess(projectMilestones));

        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).withCompetition(competitionId).build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();


        ProjectFinanceChangesViewModel result = populator.getProjectFinanceChangesViewModel(true, project, organisation);

        assertThat(result.getMilestoneDifferences().getMilestoneDifferences()).hasSize(2);
        assertThat(result.getMilestoneDifferences().getMilestoneDifferences().get(0).isSame()).isTrue();
        assertThat(result.getMilestoneDifferences().getMilestoneDifferences().get(1).isSame()).isTrue();
    }

    @Test
    public void shouldDetermineAdditionInMilestones() {

        List<ApplicationProcurementMilestoneResource> applicationMilestones = asList(
                newApplicationProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newApplicationProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build()
        );

        List<ProjectProcurementMilestoneResource> projectMilestones = asList(
                newProjectProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newProjectProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build(),
                newProjectProcurementMilestoneResource().withMonth(3).withDescription("desc3").withPayment(new BigInteger("3000")).build()
        );

        when(applicationProcurementMilestoneRestService.getByApplicationIdAndOrganisationId(applicationId, organisationId)).thenReturn(restSuccess(applicationMilestones));
        when(projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(restSuccess(projectMilestones));

        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).withCompetition(competitionId).build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();


        ProjectFinanceChangesViewModel result = populator.getProjectFinanceChangesViewModel(true, project, organisation);

        assertThat(result.getMilestoneDifferences().getMilestoneDifferences()).hasSize(3);
        assertThat(result.getMilestoneDifferences().getMilestoneDifferences().get(0).isSame()).isTrue();
        assertThat(result.getMilestoneDifferences().getMilestoneDifferences().get(1).isSame()).isTrue();
        MilestoneChangeViewModel diff = result.getMilestoneDifferences().getMilestoneDifferences().get(2);
        assertThat(diff.getDescription()).isEqualTo("desc3");
        assertThat(diff.getPaymentSubmitted()).isZero();
        assertThat(diff.getPaymentUpdated()).isEqualTo(new BigInteger("3000"));
        assertThat(diff.getMonthSubmitted()).isZero();
        assertThat(diff.getMonthUpdated()).isEqualTo(3);
        assertThat(diff.isAdded()).isTrue();
    }

    @Test
    public void shouldDetermineRemovalInMilestones() {

        List<ApplicationProcurementMilestoneResource> applicationMilestones = asList(
                newApplicationProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newApplicationProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build()
        );

        List<ProjectProcurementMilestoneResource> projectMilestones = asList(
                newProjectProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build()
        );

        when(applicationProcurementMilestoneRestService.getByApplicationIdAndOrganisationId(applicationId, organisationId)).thenReturn(restSuccess(applicationMilestones));
        when(projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(restSuccess(projectMilestones));

        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).withCompetition(competitionId).build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();


        ProjectFinanceChangesViewModel result = populator.getProjectFinanceChangesViewModel(true, project, organisation);

        assertThat(result.getMilestoneDifferences().getMilestoneDifferences()).hasSize(2);
        assertThat(result.getMilestoneDifferences().getMilestoneDifferences().get(0).isSame()).isTrue();
        MilestoneChangeViewModel diff = result.getMilestoneDifferences().getMilestoneDifferences().get(1);
        assertThat(diff.getDescription()).isEqualTo("desc2");
        assertThat(diff.getPaymentSubmitted()).isEqualTo(new BigInteger("2000"));
        assertThat(diff.getPaymentUpdated()).isZero();
        assertThat(diff.getMonthSubmitted()).isEqualTo(2);
        assertThat(diff.getMonthUpdated()).isZero();
        assertThat(diff.isRemoved()).isTrue();
    }

    @Test
    public void shouldDetermineChangeInMilestones() {

        List<ApplicationProcurementMilestoneResource> applicationMilestones = asList(
                newApplicationProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newApplicationProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build()
        );

        List<ProjectProcurementMilestoneResource> projectMilestones = asList(
                newProjectProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newProjectProcurementMilestoneResource().withMonth(3).withDescription("desc2").withPayment(new BigInteger("3000")).build()

        );

        when(applicationProcurementMilestoneRestService.getByApplicationIdAndOrganisationId(applicationId, organisationId)).thenReturn(restSuccess(applicationMilestones));
        when(projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(projectId, organisationId)).thenReturn(restSuccess(projectMilestones));

        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).withCompetition(competitionId).build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();


        ProjectFinanceChangesViewModel result = populator.getProjectFinanceChangesViewModel(true, project, organisation);

        assertThat(result.getMilestoneDifferences().getMilestoneDifferences()).hasSize(2);
        assertThat(result.getMilestoneDifferences().getMilestoneDifferences().get(0).isSame()).isTrue();
        MilestoneChangeViewModel diff = result.getMilestoneDifferences().getMilestoneDifferences().get(1);
        assertThat(diff.getDescription()).isEqualTo("desc2");
        assertThat(diff.getPaymentSubmitted()).isEqualTo(new BigInteger("2000"));
        assertThat(diff.getPaymentUpdated()).isEqualTo(new BigInteger("3000"));
        assertThat(diff.getMonthSubmitted()).isEqualTo(2);
        assertThat(diff.getMonthUpdated()).isEqualTo(3);
        assertThat(diff.isUpdated()).isTrue();
    }

    @Test
    public void shouldVerifyNonFECCostRowEntriesDisplay() {
        CompetitionResource competitionResource = newCompetitionResource().withId(competitionId).build();
        OrganisationResource organisationResource = newOrganisationResource().withId(organisationId).build();

       Map<FinanceRowType, FinanceRowCostCategory> financeOrganisationDetails = asMap(
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
