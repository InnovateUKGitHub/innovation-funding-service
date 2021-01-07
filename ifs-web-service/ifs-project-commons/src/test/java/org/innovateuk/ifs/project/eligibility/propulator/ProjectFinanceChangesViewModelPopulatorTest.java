package org.innovateuk.ifs.project.eligibility.propulator;

import org.innovateuk.ifs.application.finance.viewmodel.MilestoneChangeViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ProjectFinanceChangesViewModel;
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
import org.innovateuk.ifs.procurement.milestone.resource.ApplicationProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ApplicationProcurementMilestoneRestService;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.eligibility.populator.ProjectFinanceChangesViewModelPopulator;
import org.innovateuk.ifs.project.finance.resource.FinanceCheckEligibilityResource;
import org.innovateuk.ifs.project.finance.service.FinanceCheckRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ApplicationFinanceResourceBuilder.newApplicationFinanceResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.procurement.milestone.builder.ApplicationProcurementMilestoneBuilder.newApplicationProcurementMilestoneResource;
import static org.innovateuk.ifs.procurement.milestone.builder.ProjectProcurementMilestoneBuilder.newProjectProcurementMilestoneResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.finance.builder.FinanceCheckEligibilityResourceBuilder.newFinanceCheckEligibilityResource;
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

        ApplicationFinanceResource applicationFinanceResource = newApplicationFinanceResource().withFinanceOrganisationDetails(financeOrganisationDetails).build();
        when(applicationFinanceRestService.getFinanceDetails(applicationId, organisationId)).thenReturn(restSuccess(applicationFinanceResource));

        CompetitionResource competitionResource = newCompetitionResource().withFundingType(FundingType.PROCUREMENT).build();
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));
    }

    @Test
    public void shouldDetermineNoChangesInMilestones() {

        List<ApplicationProcurementMilestoneResource> applicationMilestones = Arrays.asList(
                newApplicationProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newApplicationProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build()
        );

        List<ProjectProcurementMilestoneResource> projectMilestones = Arrays.asList(
                newProjectProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newProjectProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build()
        );

        when(applicationProcurementMilestoneRestService.getByApplicationId(applicationId)).thenReturn(restSuccess(applicationMilestones));
        when(projectProcurementMilestoneRestService.getByProjectId(projectId)).thenReturn(restSuccess(projectMilestones));

        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).withCompetition(competitionId).build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();


        ProjectFinanceChangesViewModel result = populator.getProjectFinanceChangesViewModel(true, project, organisation);

        assertThat(result.getMilestoneDifferences()).hasSize(0);
    }

    @Test
    public void shouldDetermineAdditionInMilestones() {

        List<ApplicationProcurementMilestoneResource> applicationMilestones = Arrays.asList(
                newApplicationProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newApplicationProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build()
        );

        List<ProjectProcurementMilestoneResource> projectMilestones = Arrays.asList(
                newProjectProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newProjectProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build(),
                newProjectProcurementMilestoneResource().withMonth(3).withDescription("desc3").withPayment(new BigInteger("3000")).build()
        );

        when(applicationProcurementMilestoneRestService.getByApplicationId(applicationId)).thenReturn(restSuccess(applicationMilestones));
        when(projectProcurementMilestoneRestService.getByProjectId(projectId)).thenReturn(restSuccess(projectMilestones));

        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).withCompetition(competitionId).build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();


        ProjectFinanceChangesViewModel result = populator.getProjectFinanceChangesViewModel(true, project, organisation);

        assertThat(result.getMilestoneDifferences()).hasSize(1);
        MilestoneChangeViewModel diff = result.getMilestoneDifferences().get(0);
        assertThat(diff.getDescription()).isEqualTo("desc3");
        assertThat(diff.getPaymentSubmitted()).isNull();
        assertThat(diff.getPaymentUpdated()).isEqualTo(new BigInteger("3000"));
        assertThat(diff.getMonthSubmitted()).isNull();
        assertThat(diff.getMonthUpdated()).isEqualTo(3);
        assertThat(diff.isAdded()).isTrue();
    }

    @Test
    public void shouldDetermineRemovalInMilestones() {

        List<ApplicationProcurementMilestoneResource> applicationMilestones = Arrays.asList(
                newApplicationProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newApplicationProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build()
        );

        List<ProjectProcurementMilestoneResource> projectMilestones = Arrays.asList(
                newProjectProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build()
        );

        when(applicationProcurementMilestoneRestService.getByApplicationId(applicationId)).thenReturn(restSuccess(applicationMilestones));
        when(projectProcurementMilestoneRestService.getByProjectId(projectId)).thenReturn(restSuccess(projectMilestones));

        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).withCompetition(competitionId).build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();


        ProjectFinanceChangesViewModel result = populator.getProjectFinanceChangesViewModel(true, project, organisation);

        assertThat(result.getMilestoneDifferences()).hasSize(1);
        MilestoneChangeViewModel diff = result.getMilestoneDifferences().get(0);
        assertThat(diff.getDescription()).isEqualTo("desc2");
        assertThat(diff.getPaymentSubmitted()).isEqualTo(new BigInteger("2000"));
        assertThat(diff.getPaymentUpdated()).isNull();
        assertThat(diff.getMonthSubmitted()).isEqualTo(2);
        assertThat(diff.getMonthUpdated()).isNull();
        assertThat(diff.isRemoved()).isTrue();
    }

    @Test
    public void shouldDetermineChangeInMilestones() {

        List<ApplicationProcurementMilestoneResource> applicationMilestones = Arrays.asList(
                newApplicationProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newApplicationProcurementMilestoneResource().withMonth(2).withDescription("desc2").withPayment(new BigInteger("2000")).build()
        );

        List<ProjectProcurementMilestoneResource> projectMilestones = Arrays.asList(
                newProjectProcurementMilestoneResource().withMonth(1).withDescription("desc1").withPayment(new BigInteger("1000")).build(),
                newProjectProcurementMilestoneResource().withMonth(3).withDescription("desc2").withPayment(new BigInteger("3000")).build()

        );

        when(applicationProcurementMilestoneRestService.getByApplicationId(applicationId)).thenReturn(restSuccess(applicationMilestones));
        when(projectProcurementMilestoneRestService.getByProjectId(projectId)).thenReturn(restSuccess(projectMilestones));

        ProjectResource project = newProjectResource().withId(projectId).withApplication(applicationId).withCompetition(competitionId).build();
        OrganisationResource organisation = newOrganisationResource().withId(organisationId).build();


        ProjectFinanceChangesViewModel result = populator.getProjectFinanceChangesViewModel(true, project, organisation);

        assertThat(result.getMilestoneDifferences()).hasSize(1);
        MilestoneChangeViewModel diff = result.getMilestoneDifferences().get(0);
        assertThat(diff.getDescription()).isEqualTo("desc2");
        assertThat(diff.getPaymentSubmitted()).isEqualTo(new BigInteger("2000"));
        assertThat(diff.getPaymentUpdated()).isEqualTo(new BigInteger("3000"));
        assertThat(diff.getMonthSubmitted()).isEqualTo(2);
        assertThat(diff.getMonthUpdated()).isEqualTo(3);
        assertThat(diff.isUpdated()).isTrue();
    }

}
