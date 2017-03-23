package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.builder.QuestionResourceBuilder;
import org.innovateuk.ifs.application.builder.SectionResourceBuilder;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.view.FinanceModelManager;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.application.resource.QuestionType;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationModelPopulatorTest {

    @InjectMocks
    private ApplicationModelPopulator applicationModelPopulator;

    @Mock
    protected UserService userService;

    @Mock
    protected QuestionService questionService;

    @Mock
    protected ProcessRoleService processRoleService;

    @Mock
    protected SectionService sectionService;

    @Mock
    protected ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;;

    @Mock
    protected OrganisationService organisationService;

    @Mock
    protected FinanceHandler financeHandler;

    @Mock
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Mock
    protected UserRestService userRestService;

    @Test
    public void testAddApplicationAndSections() {
        LocalDate startDate = LocalDate.now();
        ApplicationResource application = ApplicationResourceBuilder.newApplicationResource()
            .withStartDate(startDate).build();
        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();
        Long userId = 1L;
        Long organisationId = 3L;
        OrganisationResource organisationResource = newOrganisationResource()
                .withId(organisationId).build();
        Optional<OrganisationResource> userOrganisation = Optional.of(organisationResource);
        UserResource user = UserResourceBuilder.newUserResource()
                .withId(userId).build();
        Optional<SectionResource> section = Optional.of(SectionResourceBuilder.newSectionResource().build());
        Optional<Long> currentQuestionId = Optional.of(2L);
        Model model = mock(Model.class);
        ApplicationForm form = new ApplicationForm();
        List<ProcessRoleResource> userApplicationRoles = ProcessRoleResourceBuilder.newProcessRoleResource()
                .withUser(user).withOrganisation(organisationId).build(1);
        long leadApplicantId = 4L;
        UserResource leadApplicant = UserResourceBuilder.newUserResource()
                .withId(leadApplicantId).build();
        ProcessRoleResource leadApplicantProcessRole  = ProcessRoleResourceBuilder.newProcessRoleResource()
                .withUser(leadApplicant).build();

        when(organisationService.getOrganisationById(organisationId)).thenReturn(organisationResource);
        when(userService.getLeadApplicantProcessRoleOrNull(application)).thenReturn(leadApplicantProcessRole);
        when(userService.findById(leadApplicantId)).thenReturn(leadApplicant);
        when(processRoleService.findProcessRolesByApplicationId(application.getId())).thenReturn(userApplicationRoles);


        applicationModelPopulator.addApplicationAndSections(application, competition, userId, section, currentQuestionId, model, form);

        //Verify added attributes
        verify(model).addAttribute("currentApplication", application);
        verify(model).addAttribute("currentCompetition", competition);
        verify(model).addAttribute("userOrganisation", userOrganisation.orElse(null));
        verify(model).addAttribute(ApplicationModelPopulator.MODEL_ATTRIBUTE_FORM, form);
        verify(model).addAttribute("userIsLeadApplicant", false);
        verify(model).addAttribute("leadApplicant", leadApplicant);
        verify(model).addAttribute("completedQuestionsPercentage", application.getCompletion());
        verifyNoMoreInteractions(model);

        //Verify other model calls
        verify(applicationSectionAndQuestionModelPopulator).addQuestionsDetails(model, application, form);
        verify(applicationSectionAndQuestionModelPopulator).addMappedSectionsDetails(model, application, competition, section, userOrganisation);
        verify(applicationSectionAndQuestionModelPopulator).addAssignableDetails(model, application, organisationResource, userId, section, currentQuestionId);
        verify(applicationSectionAndQuestionModelPopulator).addCompletedDetails(model, application, userOrganisation);
        verify(applicationSectionAndQuestionModelPopulator).addSectionDetails(model, section);

        //Verify form inputs
        Map<String, String> formInputs = form.getFormInput();
        assertThat(formInputs.get("application_details-title"), equalTo(application.getName()));
        assertThat(formInputs.get("application_details-duration"), equalTo(String.valueOf(application.getDurationInMonths())));
        assertThat(formInputs.get("application_details-startdate_day"), equalTo(String.valueOf(application.getStartDate().getDayOfMonth())));
        assertThat(formInputs.get("application_details-startdate_month"), equalTo(String.valueOf(application.getStartDate().getMonthValue())));
        assertThat(formInputs.get("application_details-startdate_year"), equalTo(String.valueOf(application.getStartDate().getYear())));
    }

    @Test
    public void testAddOrganisationAndUserFinanceDetails() {
        Long competitionId = 1L;
        Long applicationId = 2L;
        Long userId = 3L;
        Long organisationId = 3L;
        Long userOrganisationId = 45L;
        OrganisationResource userOrganisation = newOrganisationResource().withId(userOrganisationId).build();

        UserResource user = UserResourceBuilder.newUserResource()
                .withId(userId).build();
        Model model = mock(Model.class);
        ApplicationForm form = new ApplicationForm();
        SectionResource financeSection = SectionResourceBuilder.newSectionResource().build();
        List<QuestionResource> costsQuestions = QuestionResourceBuilder.newQuestionResource().build(2);
        Long organisationType = 1L;
        FinanceModelManager financeModelManager = mock(FinanceModelManager.class);

        when(sectionService.getFinanceSection(competitionId)).thenReturn(financeSection);
        when(questionService.getQuestionsBySectionIdAndType(financeSection.getId(), QuestionType.COST)).thenReturn(costsQuestions);
        when(organisationService.getOrganisationType(user.getId(), applicationId)).thenReturn(organisationType);

        when(organisationService.getOrganisationForUser(user.getId())).thenReturn(userOrganisation);
        when(financeHandler.getFinanceModelManager(organisationType)).thenReturn(financeModelManager);

        ProcessRoleResource processRole  = ProcessRoleResourceBuilder.newProcessRoleResource().withOrganisation().withUser(user).build();
        when(userRestService.findProcessRole(user.getId(), applicationId)).thenReturn(restSuccess(processRole));

        applicationModelPopulator.addOrganisationAndUserFinanceDetails(competitionId, applicationId, user, model, form, organisationId);

        //verify model attributes
        verify(model).addAttribute("currentUser", user);
        verifyNoMoreInteractions(model);

        //Verify model calls
        verify(applicationFinanceOverviewModelManager).addFinanceDetails(model, competitionId, applicationId, Optional.of(organisationId));
        verify(financeModelManager).addOrganisationFinanceDetails(model, applicationId, costsQuestions, user.getId(), form, organisationId);
    }
}
