package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.procurement.milestone.service.ProjectProcurementMilestoneRestService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceRestService;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel;
import org.innovateuk.ifs.project.grantofferletter.viewmodel.ProcurementGrantOfferLetterTemplateViewModel.ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.procurement.milestone.builder.ProjectProcurementMilestoneResourceBuilder.newProjectProcurementMilestoneResource;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcurementGrantOfferLetterTemplatePopulatorTest {

    @InjectMocks
    private ProcurementGrantOfferLetterTemplatePopulator populator;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private ProjectProcurementMilestoneRestService projectProcurementMilestoneRestService;

    @Mock
    private ProjectFinanceRestService projectFinanceRestService;

    @Mock
    private ProjectRestService projectRestService;

    @Test
    public void populate() {
        long applicationId = 4L;
        long projectId = 5L;
        LocalDate projectStartDate = LocalDate.now();

        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withTargetStartDate(projectStartDate)
                .withDuration(4L)
                .withApplication(applicationId).build();
        CompetitionResource competition = newCompetitionResource().build();
        Long firstOrgId = 9L;
        Long secondOrgId = 10L;
        String firstPartnerName = "one";
        String secondPartnerName = "two";
        List<PartnerOrganisationResource> partnerOrgs = newPartnerOrganisationResource()
                .withOrganisation(firstOrgId, secondOrgId)
                .withOrganisationName(firstPartnerName, secondPartnerName)
                .build(2);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(partnerOrgs));
        List<ProjectProcurementMilestoneResource> milestones = newProjectProcurementMilestoneResource()
                .withMonth(1, 2, 2)
                .withPayment(new BigInteger("100"), new BigInteger("200"), new BigInteger("300"))
                .build(3);
        when(projectProcurementMilestoneRestService.getByProjectIdAndOrganisationId(projectId, firstOrgId)).thenReturn(restSuccess(milestones));
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().build();
        when(projectFinanceRestService.getProjectFinance(projectId, firstOrgId)).thenReturn(restSuccess(projectFinanceResource));
        String projectManagerName = "Bill";
        ProjectUserResource projectManager = newProjectUserResource().withUserName(projectManagerName).build();
        when(projectRestService.getProjectManager(projectId)).thenReturn(restSuccess(projectManager));

        ProcurementGrantOfferLetterTemplateViewModel result = populator.populate(project, competition);

        assertThat(result.getApplicationId()).isEqualTo(applicationId);
        assertThat(result.getOrganisationName()).isEqualTo(firstPartnerName);
        assertThat(result.getProjectManagerName()).isEqualTo(projectManagerName);
        assertThat(result.getMilestones()).hasSize(3);
        assertThat(result.getMilestoneMonths()).hasSize(4);
        ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel firstMilestoneMonth = result.getMilestoneMonths().get(0);
        assertThat(firstMilestoneMonth.getMonth()).isEqualTo(1);
        assertThat(firstMilestoneMonth.getNumbers()).isEqualTo("1");
        assertThat(firstMilestoneMonth.getTotal()).isEqualTo(new BigInteger("100"));
        ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel secondMilestoneMonth = result.getMilestoneMonths().get(1);
        assertThat(secondMilestoneMonth.getMonth()).isEqualTo(2);
        assertThat(secondMilestoneMonth.getNumbers()).isEqualTo("2, 3");
        assertThat(secondMilestoneMonth.getTotal()).isEqualTo(new BigInteger("500"));
        ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel thirdMilestoneMonth = result.getMilestoneMonths().get(2);
        assertThat(thirdMilestoneMonth.getMonth()).isEqualTo(3);
        assertThat(thirdMilestoneMonth.getNumbers()).isEqualTo("");
        assertThat(thirdMilestoneMonth.getTotal()).isEqualTo(BigInteger.ZERO);
        ProcurementGrantOfferLetterTemplateMilestoneMonthEntryViewModel fourthMilestoneMonth = result.getMilestoneMonths().get(3);
        assertThat(fourthMilestoneMonth.getMonth()).isEqualTo(4);
        assertThat(fourthMilestoneMonth.getNumbers()).isEqualTo("");
        assertThat(fourthMilestoneMonth.getTotal()).isEqualTo(BigInteger.ZERO);
    }

}
