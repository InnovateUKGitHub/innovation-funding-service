package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.view.DefaultFinanceModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceViewHandlerProvider;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceOverviewViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.Form;
import org.innovateuk.ifs.form.resource.*;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.*;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.RESEARCH_CATEGORY;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OpenFinanceSectionModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private OpenApplicationFinanceSectionModelPopulator populator;

    @Mock
    private QuestionService questionService;

    @Mock
    private QuestionRestService questionRestService;

    @Mock
    private SectionService sectionService;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private FormInputRestService formInputRestService;

    @Mock
    private FormInputResponseService formInputResponseService;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private InviteRestService inviteRestService;

    @Mock
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    @Mock
    private FinanceViewHandlerProvider financeViewHandlerProvider;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Mock
    private UserRestService userRestService;

    @Before
    public void setUp() {
        super.setup();
    }

    @Test
    public void testPopulateModelWithValidObjects() throws Exception {
        Long competitionId = 1L, applicationId = 23L;

        ApplicationResource application = newApplicationResource().withId(applicationId).withCompetition(competitionId).build();

        ApplicationForm applicationForm = new ApplicationForm();
        applicationForm.setApplication(application);
        applicationForm.setStateAidAgreed(Boolean.TRUE);
        applicationForm.setTermsAgreed(Boolean.TRUE);


        List<Long> questionResourceList = asList(34L, 35L);
        SectionResource section = newSectionResource().withChildSections(Collections.emptyList())
                .withQuestions(questionResourceList)
                .withType(SectionType.FINANCE)
                .withCompetition(competitionId)
                .build();
        UserResource user = newUserResource().build();
        CompetitionResource competition = newCompetitionResource().withId(competitionId).withUseNewApplicantMenu(true).build();
        List<SectionResource> allSections = newSectionResource().withCompetition(competitionId).build(5);
        List<FormInputResource> formInputs = newFormInputResource().withQuestion(section.getQuestions().get(0)).build(2);
        setupServices(competition, application, user, formInputs);

        ProcessRoleResource processRole  = ProcessRoleResourceBuilder.newProcessRoleResource().withOrganisation().withUser(user).withRole(LEADAPPLICANT).build();
        when(userRestService.findProcessRole(user.getId(), applicationId)).thenReturn(restSuccess(processRole));
        when(organisationService.getOrganisationById(anyLong())).thenReturn(newOrganisationResource().withId(processRole.getOrganisationId()).build());
        when(userRestService.retrieveUserById(user.getId())).thenReturn(restSuccess(user));
        ApplicantResource applicant = newApplicantResource().withProcessRole(processRole).withOrganisation(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build()).build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource().withApplication(application).withCompetition(competition).withCurrentApplicant(applicant).withApplicants(asList(applicant)).withSection(section).withCurrentUser(user).build();
        BaseSectionViewModel result = populator.populateModel(applicationForm, model, bindingResult, applicantSection);

        assertEquals(OpenFinanceSectionViewModel.class, result.getClass());
        OpenFinanceSectionViewModel viewModel = (OpenFinanceSectionViewModel) result;

        assertEquals(application, viewModel.getApplication().getCurrentApplication());
        assertEquals(Boolean.TRUE, viewModel.getHasFinanceSection());
        assertEquals(section.getName(), viewModel.getTitle());
        assertEquals(competition, viewModel.getApplication().getCurrentCompetition());
        assertEquals(section, viewModel.getCurrentSection());
        assertEquals(section.getId(), viewModel.getCurrentSectionId());
        assertEquals(Boolean.TRUE, viewModel.getUserIsLeadApplicant());
        assertEquals(user, viewModel.getLeadApplicant());
    }

    @Test
    public void testPopulateModelWithInvalidObjects() throws Exception {
        Long competitionId = Long.MIN_VALUE, applicationId = Long.MAX_VALUE;

        ApplicationResource application = newApplicationResource().withId(applicationId).withCompetition(competitionId).build();

        ApplicationForm applicationForm = new ApplicationForm();
        applicationForm.setApplication(application);
        applicationForm.setStateAidAgreed(Boolean.FALSE);
        applicationForm.setTermsAgreed(Boolean.FALSE);


        List<Long> questionResourceList = asList();
        SectionResource section = newSectionResource().withChildSections(Collections.emptyList())
                .withType(SectionType.FINANCE)
                .withQuestions(questionResourceList)
                .withCompetition(231L)
                .build();
        UserResource user = newUserResource().build();
        CompetitionResource competition = newCompetitionResource().withId(321L).withUseNewApplicantMenu(true).build();
        List<SectionResource> allSections = newSectionResource().withCompetition(132L).build(1);
        List<FormInputResource> formInputs = newFormInputResource().withQuestion(123L).build(1);
        setupServices(competition, application, user, formInputs);

        ProcessRoleResource processRole  = ProcessRoleResourceBuilder.newProcessRoleResource().withOrganisation().withUser(user).withRole(LEADAPPLICANT).build();
        when(userRestService.findProcessRole(user.getId(), applicationId)).thenReturn(restSuccess(processRole));
        when(organisationService.getOrganisationById(anyLong())).thenReturn(newOrganisationResource().withId(processRole.getOrganisationId()).build());
        when(userRestService.retrieveUserById(user.getId())).thenReturn(restSuccess(user));

        ApplicantResource applicant = newApplicantResource().withProcessRole(processRole).withOrganisation(newOrganisationResource().withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build()).build();
        ApplicantSectionResource applicantSection = newApplicantSectionResource().withApplication(application).withCompetition(competition).withCurrentApplicant(applicant).withApplicants(asList(applicant)).withSection(section).withCurrentUser(user).build();
        BaseSectionViewModel result = populator.populateModel(applicationForm, model, bindingResult, applicantSection);

        assertEquals(OpenFinanceSectionViewModel.class, result.getClass());
        OpenFinanceSectionViewModel viewModel = (OpenFinanceSectionViewModel) result;

        assertEquals(application, viewModel.getApplication().getCurrentApplication());
        assertEquals(Boolean.TRUE, viewModel.getHasFinanceSection());
        assertEquals(section.getName(), viewModel.getTitle());
        assertEquals(competition, viewModel.getApplication().getCurrentCompetition());
        assertEquals(section, viewModel.getCurrentSection());
        assertEquals(section.getId(), viewModel.getCurrentSectionId());
        assertEquals(Boolean.TRUE, viewModel.getUserIsLeadApplicant());
        assertEquals(user, viewModel.getLeadApplicant());
    }

    private void setupServices(CompetitionResource competitionResource, ApplicationResource applicationResource, UserResource userResource, List<FormInputResource> formInputs) {
        when(competitionRestService.getCompetitionById(applicationResource.getCompetition())).thenReturn(restSuccess(competitionResource));

        InviteOrganisationResource inviteOrg1 = new InviteOrganisationResource();
        inviteOrg1.setId(234L);
        inviteOrg1.setOrganisation(245L);
        inviteOrg1.setOrganisationName("Awesomefy");
        inviteOrg1.setOrganisationNameConfirmed("New name");
        inviteOrg1.setInviteResources(newApplicationInviteResource().build(2));

        when(inviteRestService.getInvitesByApplication(applicationResource.getId())).thenReturn(RestResult.restSuccess(asList(inviteOrg1)));
        when(userService.isLeadApplicant(userResource.getId(), applicationResource)).thenReturn(Boolean.TRUE);

        when(sectionService.getCompletedSectionsByOrganisation(applicationResource.getId())).thenReturn(asMap(1L, new HashSet<>()));

        ProcessRoleResource leadApplicantProcessRole = newProcessRoleResource().withUser(userResource).build();

        when(userService.getLeadApplicantProcessRoleOrNull(applicationResource.getId())).thenReturn(leadApplicantProcessRole);
        when(userRestService.retrieveUserById(leadApplicantProcessRole.getUser())).thenReturn(restSuccess(userResource));

        when(formInputRestService.getByCompetitionIdAndScope(competitionResource.getId(), APPLICATION)).thenReturn(restSuccess(formInputs));

        when(organisationService.getOrganisationType(userResource.getId(), applicationResource.getId())).thenReturn(OrganisationTypeEnum.BUSINESS.getId());
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(Optional.of(newOrganisationResource().build()));

        DefaultFinanceModelManager financeManager = mock(DefaultFinanceModelManager.class);
        when(financeViewHandlerProvider.getFinanceModelManager(OrganisationTypeEnum.BUSINESS.getId())).thenReturn(financeManager);

        QuestionResource question = newQuestionResource().build();
        when(questionService.getQuestionByCompetitionIdAndFormInputType(anyLong(), eq(FormInputType.APPLICATION_DETAILS))).thenReturn(ServiceResult.serviceSuccess(question));
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(anyLong(), eq(RESEARCH_CATEGORY))).thenReturn(restSuccess(question));
        Map<Long, QuestionStatusResource> statuses = new HashMap<>();
        statuses.put(question.getId(), newQuestionStatusResource().withMarkedAsComplete(true).build());
        when(questionService.getQuestionStatusesForApplicationAndOrganisation(eq(applicationResource.getId()), anyLong())).thenReturn(statuses);

        List<FormInputResponseResource> formInputResponseResources = new ArrayList<>();
        when(formInputResponseRestService.getResponsesByApplicationId(applicationResource.getId())).thenReturn(restSuccess(formInputResponseResources));
        when(formInputRestService.getByCompetitionIdAndScope(applicationResource.getCompetition(), APPLICATION)).thenReturn(restSuccess(new ArrayList<>()));

        ApplicationFinanceOverviewViewModel financeOverviewViewModel = new ApplicationFinanceOverviewViewModel();
        when(applicationFinanceOverviewModelManager.getFinanceDetailsViewModel(competitionResource.getId(), applicationResource.getId())).thenReturn(financeOverviewViewModel);

        FinanceViewModel financeViewModel = new FinanceViewModel();
        financeViewModel.setOrganisationGrantClaimPercentage(76);

        when(financeManager.getFinanceViewModel(anyLong(), anyList(), anyLong(), any(Form.class), anyLong())).thenReturn(financeViewModel);
    }
}