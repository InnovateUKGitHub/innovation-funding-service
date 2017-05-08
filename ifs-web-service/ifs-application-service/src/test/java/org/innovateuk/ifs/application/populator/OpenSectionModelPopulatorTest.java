package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.form.ApplicationForm;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.SectionResource;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.QuestionService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.ProcessRoleService;
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
import static org.innovateuk.ifs.application.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OpenSectionModelPopulatorTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private OpenSectionModelPopulator populator;

    @Mock
    private QuestionService questionService;

    @Mock
    private SectionService sectionService;

    @Mock
    private ProcessRoleService processRoleService;

    @Mock
    private FormInputRestService formInputRestService;

    @Mock
    private FormInputResponseService formInputResponseService;

    @Mock
    private FormInputResponseRestService formInputResponseRestService;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private InviteRestService inviteRestService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Before
    public void setUp() {
        super.setUp();

    }

    @Test
    public void testPopulateModelWithValidObjects() throws Exception {
        Long competitionId = 1L, applicationId = 23L;

        Long organisationId = 245L;

        ApplicationResource application = newApplicationResource().withId(applicationId).withCompetition(competitionId).build();

        ApplicationForm applicationForm = new ApplicationForm();
        applicationForm.setApplication(application);
        applicationForm.setStateAidAgreed(Boolean.TRUE);
        applicationForm.setTermsAgreed(Boolean.TRUE);


        List<Long> questionResourceList = asList(34L, 35L);
        SectionResource section = newSectionResource().withChildSections(Collections.emptyList()).withQuestions(questionResourceList).withCompetition(competitionId).build();
        UserResource user = newUserResource().build();
        CompetitionResource competition = newCompetitionResource().withId(competitionId).build();
        List<SectionResource> allSections = newSectionResource().withCompetition(competitionId).build(5);
        List<FormInputResource> formInputs = newFormInputResource().withQuestion(section.getQuestions().get(0)).build(2);
        setupServices(competition, application, user, formInputs);

        BaseSectionViewModel result = populator.populateModel(applicationForm, model, application, section, user, bindingResult, allSections, organisationId);

        assertEquals(OpenSectionViewModel.class, result.getClass());
        OpenSectionViewModel viewModel = (OpenSectionViewModel) result;

        assertEquals(application, viewModel.getApplication().getCurrentApplication());
        assertEquals(Boolean.FALSE, viewModel.getHasFinanceSection());
        assertEquals(section.getName(), viewModel.getTitle());
        assertEquals(competition, viewModel.getApplication().getCurrentCompetition());
        assertEquals(section, viewModel.getCurrentSection());
        assertEquals(section.getId(), viewModel.getCurrentSectionId());
        assertEquals(Boolean.TRUE, viewModel.getUserIsLeadApplicant());
        assertEquals(user, viewModel.getLeadApplicant());
    }

    @Test
    public void testPopulateModelWithInvalidObjects() throws Exception {
        Long organisationId = 245L;

        Long competitionId = Long.MIN_VALUE, applicationId = Long.MAX_VALUE;

        ApplicationResource application = newApplicationResource().withId(applicationId).withCompetition(competitionId).build();

        ApplicationForm applicationForm = new ApplicationForm();
        applicationForm.setApplication(application);
        applicationForm.setStateAidAgreed(Boolean.TRUE);
        applicationForm.setTermsAgreed(Boolean.TRUE);


        List<Long> questionResourceList = asList(90L);
        SectionResource section = newSectionResource().withChildSections(Collections.emptyList()).withQuestions(questionResourceList).withCompetition(231L).build();
        UserResource user = newUserResource().build();
        CompetitionResource competition = newCompetitionResource().withId(321L).build();
        List<SectionResource> allSections = newSectionResource().withCompetition(132L).build(0);
        List<FormInputResource> formInputs = newFormInputResource().withQuestion(123L).build(0);
        setupServices(competition, application, user, formInputs);

        BaseSectionViewModel result = populator.populateModel(applicationForm, model, application, section, user, bindingResult, allSections, organisationId);

        assertEquals(OpenSectionViewModel.class, result.getClass());
        OpenSectionViewModel viewModel = (OpenSectionViewModel) result;

        assertEquals(null, viewModel.getApplication().getCurrentApplication());
        assertEquals(null, viewModel.getHasFinanceSection());
        assertEquals(null, viewModel.getTitle());
        assertEquals(null, viewModel.getApplication().getCurrentCompetition());
        assertEquals(null, viewModel.getCurrentSection());
        assertEquals(null, viewModel.getCurrentSectionId());
        assertEquals(null, viewModel.getUserIsLeadApplicant());
        assertEquals(null, viewModel.getLeadApplicant());
    }

    private void setupServices(CompetitionResource competitionResource, ApplicationResource applicationResource, UserResource userResource, List<FormInputResource> formInputs) {

        Long organisationId = 245L;

        when(competitionService.getById(competitionResource.getId())).thenReturn(competitionResource);

        InviteOrganisationResource inviteOrg1 = new InviteOrganisationResource();
        inviteOrg1.setId(234L);
        inviteOrg1.setOrganisation(245L);
        inviteOrg1.setOrganisationName("Awesomefy");
        inviteOrg1.setOrganisationNameConfirmed("New name");
        inviteOrg1.setInviteResources(newApplicationInviteResource().build(2));

        when(inviteRestService.getInvitesByApplication(applicationResource.getId())).thenReturn(restSuccess(asList(inviteOrg1)));

        when(userService.isLeadApplicant(userResource.getId(), applicationResource)).thenReturn(Boolean.TRUE);

        when(sectionService.getCompletedSectionsByOrganisation(applicationResource.getId())).thenReturn(asMap(1L, new HashSet<>()));

        ProcessRoleResource leadApplicantProcessRole = newProcessRoleResource().withUser(userResource).build();

        when(userService.getLeadApplicantProcessRoleOrNull(applicationResource)).thenReturn(leadApplicantProcessRole);

        when(userService.findById(leadApplicantProcessRole.getUser())).thenReturn(userResource);

        when(formInputRestService.getByCompetitionIdAndScope(competitionResource.getId(), APPLICATION)).thenReturn(restSuccess(formInputs));

        when(userService.getUserOrganisationId(userResource.getId(), applicationResource.getId())).thenReturn(organisationId);

        List<FormInputResponseResource> formInputResponseResources = new ArrayList<>();
        when(formInputResponseRestService.getResponsesByApplicationId(applicationResource.getId())).thenReturn(restSuccess(formInputResponseResources));
        when(formInputResponseService.mapFormInputResponsesToFormInput(formInputResponseResources)).thenReturn(new HashMap<>());
    }
}