package org.innovateuk.ifs.application.forms.sections.procurement.milestones.populator;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel.ApplicationProcurementMilestonesViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.SectionRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.finance.service.ApplicationFinanceRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationProcurementMilestoneViewModelPopulatorTest {

    @InjectMocks
    private ApplicationProcurementMilestoneViewModelPopulator populator;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private SectionService sectionService;

    @Mock
    private ApplicationFinanceRestService applicationFinanceRestService;

    @Mock
    private ProcessRoleRestService processRoleRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private SectionRestService sectionRestService;

    @Test
    public void populate() {
        UserResource user = newUserResource()
                .withRoleGlobal(Role.APPLICANT)
                .build();
        long applicationId = 1L;
        long organisationId = 2L;
        long sectionId = 3L;

        CompetitionResource competition = newCompetitionResource().build();
        ApplicationResource application = newApplicationResource()
                .withCompetition(competition.getId())
                .withDurationInMonths(4L)
                .build();
        ApplicationFinanceResource finance = mock(ApplicationFinanceResource.class);
        QuestionResource applicationDetails = newQuestionResource().build();
        SectionResource projectCosts = newSectionResource().build();


        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(processRoleRestService.findProcessRole(user.getId(), applicationId)).thenReturn(restSuccess(newProcessRoleResource()
            .withOrganisation(organisationId)
            .build()
        ));
        when(applicationFinanceRestService.getFinanceDetails(applicationId, organisationId)).thenReturn(restSuccess(finance));
        when(finance.getTotalFundingSought()).thenReturn(new BigDecimal("100.22"));
        when(sectionService.getCompleted(applicationId, organisationId)).thenReturn(newArrayList(sectionId));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competition.getId(), QuestionSetupType.APPLICATION_DETAILS)).thenReturn(restSuccess(applicationDetails));
        when(sectionRestService.getSectionsByCompetitionIdAndType(competition.getId(), SectionType.PROJECT_COST_FINANCES)).thenReturn(restSuccess(newArrayList(projectCosts)));

        ApplicationProcurementMilestonesViewModel viewModel = populator.populate(user, applicationId, organisationId, sectionId);

        assertThat(viewModel.getApplicationId(), is(equalTo(application.getId())));
        assertThat(viewModel.getApplicationName(), is(equalTo(application.getName())));
        assertThat(viewModel.getFinancesUrl(), is(equalTo("/application/1/form/FINANCE/2")));
        assertThat(viewModel.getDurations(), is(equalTo(newArrayList(1L, 2L, 3L, 4L))));
        assertThat(viewModel.getFundingAmount(), is(equalTo(new BigInteger("100"))));
        assertThat(viewModel.isComplete(), is(true));
        assertThat(viewModel.isOpen(), is(false));
        assertThat(viewModel.isReadOnly(), is(true));
        assertThat(viewModel.getApplicationDetailsUrl(), is(equalTo(String.format("/application/%d/form/question/%d/application-details", applicationId, applicationDetails.getId()))));
        assertThat(viewModel.isProjectCostsComplete(), is(false));
        assertThat(viewModel.isHasDurations(), is(true));
        assertThat(viewModel.isDisplayProjectCostsBanner(), is(false));
    }
}