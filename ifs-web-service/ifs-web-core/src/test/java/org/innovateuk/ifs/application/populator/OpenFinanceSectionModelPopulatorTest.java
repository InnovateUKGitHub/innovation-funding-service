package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.finance.view.ApplicationFinanceOverviewModelManager;
import org.innovateuk.ifs.application.finance.view.DefaultFinanceModelManager;
import org.innovateuk.ifs.application.finance.view.FinanceHandler;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceOverviewViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceViewModel;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.form.Form;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenFinanceSectionViewModel;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputType;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputService;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.ProcessRoleService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
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
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.application.builder.QuestionStatusResourceBuilder.newQuestionStatusResource;
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OpenFinanceSectionModelPopulatorTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private OpenApplicationFinanceSectionModelPopulator populator;

    @Mock
    private FormInputResponseService formInputResponseService;

    @Mock
    private QuestionService questionService;

    @Mock
    private SectionService sectionService;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private FormInputService formInputService;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private InviteRestService inviteRestService;

    @Mock
    private ApplicationFinanceOverviewModelManager applicationFinanceOverviewModelManager;

    @Mock
    private FinanceHandler financeHandler;

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
        super.setUp();
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
        CompetitionResource competition = newCompetitionResource().withId(competitionId).build();
        List<SectionResource> allSections = newSectionResource().withCompetition(competitionId).build(5);
        List<FormInputResource> formInputs = newFormInputResource().withQuestion(section.getQuestions().get(0)).build(2);
        setupServices(competition, application, user, formInputs);

        ProcessRoleResource processRole  = ProcessRoleResourceBuilder.newProcessRoleResource().withOrganisation().withUser(user).build();
        when(userRestService.findProcessRole(user.getId(), applicationId)).thenReturn(restSuccess(processRole));
        when(organisationService.getOrganisationById(anyLong())).thenReturn(newOrganisationResource().withId(processRole.getOrganisationId()).build());

        BaseSectionViewModel result = populator.populateModel(applicationForm, model, application, section, user, bindingResult, allSections, processRole.getOrganisationId());

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
        CompetitionResource competition = newCompetitionResource().withId(321L).build();
        List<SectionResource> allSections = newSectionResource().withCompetition(132L).build(1);
        List<FormInputResource> formInputs = newFormInputResource().withQuestion(123L).build(1);
        setupServices(competition, application, user, formInputs);

        ProcessRoleResource processRole  = ProcessRoleResourceBuilder.newProcessRoleResource().withOrganisation().withUser(user).build();
        when(userRestService.findProcessRole(user.getId(), applicationId)).thenReturn(restSuccess(processRole));
        when(organisationService.getOrganisationById(anyLong())).thenReturn(newOrganisationResource().withId(processRole.getOrganisationId()).build());

        BaseSectionViewModel result = populator.populateModel(applicationForm, model, application, section, user, bindingResult, allSections, processRole.getOrganisationId());

        assertEquals(OpenFinanceSectionViewModel.class, result.getClass());
        OpenFinanceSectionViewModel viewModel = (OpenFinanceSectionViewModel) result;

        assertEquals(application, viewModel.getApplication().getCurrentApplication());
        assertEquals(Boolean.TRUE, viewModel.getHasFinanceSection());
        assertEquals(section.getName(), viewModel.getTitle());
        assertEquals(null, viewModel.getApplication().getCurrentCompetition());
        assertEquals(section, viewModel.getCurrentSection());
        assertEquals(section.getId(), viewModel.getCurrentSectionId());
        assertEquals(Boolean.TRUE, viewModel.getUserIsLeadApplicant());
        assertEquals(user, viewModel.getLeadApplicant());
    }

    private void setupServices(CompetitionResource competitionResource, ApplicationResource applicationResource, UserResource userResource, List<FormInputResource> formInputs) {
        when(competitionService.getById(competitionResource.getId())).thenReturn(competitionResource);

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

        when(userService.getLeadApplicantProcessRoleOrNull(applicationResource)).thenReturn(leadApplicantProcessRole);
        when(userService.findById(leadApplicantProcessRole.getUser())).thenReturn(userResource);

        when(formInputService.findApplicationInputsByCompetition(competitionResource.getId())).thenReturn(formInputs);

        when(organisationService.getOrganisationType(userResource.getId(), applicationResource.getId())).thenReturn(OrganisationTypeEnum.BUSINESS.getId());
        when(organisationService.getOrganisationForUser(anyLong(), anyList())).thenReturn(Optional.of(newOrganisationResource().build()));

        DefaultFinanceModelManager financeManager = mock(DefaultFinanceModelManager.class);
        when(financeHandler.getFinanceModelManager(OrganisationTypeEnum.BUSINESS.getId())).thenReturn(financeManager);

        QuestionResource question = newQuestionResource().build();
        when(questionService.getQuestionByCompetitionIdAndFormInputType(anyLong(), eq(FormInputType.APPLICATION_DETAILS))).thenReturn(ServiceResult.serviceSuccess(question));
        Map<Long, QuestionStatusResource> statuses = new HashMap<>();
        statuses.put(question.getId(), newQuestionStatusResource().withMarkedAsComplete(true).build());
        when(questionService.getQuestionStatusesForApplicationAndOrganisation(eq(applicationResource.getId()), anyLong())).thenReturn(statuses);

        ApplicationFinanceOverviewViewModel financeOverviewViewModel = new ApplicationFinanceOverviewViewModel();
        when(applicationFinanceOverviewModelManager.getFinanceDetailsViewModel(competitionResource.getId(), applicationResource.getId())).thenReturn(financeOverviewViewModel);

        FinanceViewModel financeViewModel = new FinanceViewModel();
        financeViewModel.setOrganisationGrantClaimPercentage(76);

        when(financeManager.getFinanceViewModel(anyLong(), anyList(), anyLong(), any(Form.class), anyLong())).thenReturn(financeViewModel);
    }
}