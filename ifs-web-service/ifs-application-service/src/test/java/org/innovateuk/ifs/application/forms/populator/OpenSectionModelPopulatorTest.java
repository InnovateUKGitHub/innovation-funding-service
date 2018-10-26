package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.application.populator.OpenSectionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.form.ApplicationForm;
import org.innovateuk.ifs.form.resource.FormInputResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.resource.SectionType;
import org.innovateuk.ifs.form.service.FormInputResponseRestService;
import org.innovateuk.ifs.form.service.FormInputResponseService;
import org.innovateuk.ifs.form.service.FormInputRestService;
import org.innovateuk.ifs.invite.InviteService;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;
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
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CollaborationLevel.COLLABORATIVE;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.form.resource.SectionType.FINANCE;
import static org.innovateuk.ifs.form.resource.SectionType.OVERVIEW_FINANCES;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.BUSINESS;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OpenSectionModelPopulatorTest extends BaseUnitTest {

    private CompetitionResource competition;
    private ApplicationResource application;
    private UserResource user;
    private ApplicationForm applicationForm;
    private SectionResource section;
    private ApplicantSectionResource applicantSection;

    @InjectMocks
    private OpenSectionModelPopulator populator;

    @Mock
    private SectionService sectionService;

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
    private BindingResult bindingResult;

    @Mock
    private UserService userService;

    @Mock
    private UserRestService userRestService;

    @Mock
    private InviteService inviteService;

    @Mock
    private Model model;

    @Mock
    private ApplicationNavigationPopulator applicationNavigationPopulator;

    @Mock
    private ApplicationSectionAndQuestionModelPopulator applicationSectionAndQuestionModelPopulator;

    @Before
    public void setUp() {
        super.setup();

        competition = newCompetitionResource()
                .withCollaborationLevel(COLLABORATIVE)
                .build();

        application = newApplicationResource()
                .withCompetition(competition.getId())
                .withCollaborativeProject(true)
                .build();

        applicationForm = new ApplicationForm();
        applicationForm.setApplication(application);
        applicationForm.setStateAidAgreed(Boolean.TRUE);
        applicationForm.setTermsAgreed(Boolean.TRUE);

        List<Long> questionResourceList = asList(34L, 35L);
        section = newSectionResource()
                .withChildSections(emptyList())
                .withQuestions(questionResourceList)
                .withCompetition(competition.getId())
                .withType(SectionType.FINANCE).build();
        user = newUserResource().build();
        List<FormInputResource> formInputs = newFormInputResource().withQuestion(section.getQuestions().get(0)).build(2);

        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationType(BUSINESS.getId())
                .build();

        ApplicantResource applicant = newApplicantResource()
                .withProcessRole(newProcessRoleResource()
                        .withUser(user)
                        .withRoleName(LEADAPPLICANT.getName())
                        .build())
                .withOrganisation(organisation)
                .build();
        applicantSection = newApplicantSectionResource()
                .withApplication(application)
                .withCompetition(competition)
                .withCurrentApplicant(applicant)
                .withApplicants(asList(applicant))
                .withSection(section).withCurrentUser(user).build();

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

        InviteOrganisationResource inviteOrg1 = new InviteOrganisationResource();
        inviteOrg1.setId(234L);
        inviteOrg1.setOrganisation(245L);
        inviteOrg1.setOrganisationName("Awesomefy");
        inviteOrg1.setOrganisationNameConfirmed("New name");
        inviteOrg1.setInviteResources(newApplicationInviteResource().build(2));

        when(inviteRestService.getInvitesByApplication(application.getId())).thenReturn(restSuccess(asList(inviteOrg1)));

        when(userService.isLeadApplicant(user.getId(), application)).thenReturn(Boolean.TRUE);

        ProcessRoleResource leadApplicantProcessRole = newProcessRoleResource().withUser(user).build();

        when(userService.getLeadApplicantProcessRoleOrNull(application.getId())).thenReturn(leadApplicantProcessRole);

        when(userRestService.retrieveUserById(leadApplicantProcessRole.getUser())).thenReturn(restSuccess(user));

        when(formInputRestService.getByCompetitionIdAndScope(competition.getId(), APPLICATION)).thenReturn(restSuccess(formInputs));

        when(userService.getUserOrganisationId(user.getId(), application.getId())).thenReturn(organisation.getId());

        List<FormInputResponseResource> formInputResponseResources = new ArrayList<>();
        when(formInputResponseRestService.getResponsesByApplicationId(application.getId())).thenReturn(restSuccess(formInputResponseResources));
        when(formInputResponseService.mapFormInputResponsesToFormInput(formInputResponseResources)).thenReturn(new HashMap<>());
    }

    @Test
    public void populateModel_withValidObjects() {
        BaseSectionViewModel result = populator.populateModel(applicationForm, model, bindingResult, applicantSection);

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
        assertTrue(viewModel.isCollaborativeProject());
    }

    @Test
    public void populateModel_yourFinancesCompleteForAllOrganisations() {
        SectionResource financesSection = newSectionResource().build();
        SectionResource financeOverviewSection = newSectionResource()
                .withId(4L)
                .build();
        Set<Long> completedSectionIdsIncludingFinancesOverviewSection = asLinkedSet(1L, 2L, 3L, 4L);
        List<Long> organisationIds = asList(1L, 2L, 3L);

        Map<Long, Set<Long>> completedSectionsByOrganisations = simpleToMap(organisationIds, identity(),
                organisationId -> completedSectionIdsIncludingFinancesOverviewSection);

        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(completedSectionsByOrganisations);
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), FINANCE)).thenReturn(asList(financesSection));
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), OVERVIEW_FINANCES)).thenReturn(asList(financeOverviewSection));

        BaseSectionViewModel result = populator.populateModel(applicationForm, model, bindingResult, applicantSection);

        OpenSectionViewModel viewModel = (OpenSectionViewModel) result;

        assertTrue(viewModel.getYourFinancesCompleteForAllOrganisations());

        verify(sectionService).getCompletedSectionsByOrganisation(application.getId());
        verify(sectionService).getSectionsForCompetitionByType(application.getCompetition(), FINANCE);
        verify(sectionService).getSectionsForCompetitionByType(application.getCompetition(), OVERVIEW_FINANCES);
    }

    @Test
    public void populateModel_yourFinancesInCompleteForAnOrganisation() {
        SectionResource financesSection = newSectionResource().build();
        SectionResource financeOverviewSection = newSectionResource()
                .withId(4L)
                .build();
        Set<Long> completedSectionIdsExcludingFinancesOverviewSection = asLinkedSet(1L, 2L, 3L);
        List<Long> organisationIds = asList(1L, 2L, 3L);

        Map<Long, Set<Long>> completedSectionsByOrganisations = simpleToMap(organisationIds, identity(),
                organisationId -> completedSectionIdsExcludingFinancesOverviewSection);

        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(completedSectionsByOrganisations);
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), FINANCE)).thenReturn(asList(financesSection));
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), OVERVIEW_FINANCES)).thenReturn(asList(financeOverviewSection));

        BaseSectionViewModel result = populator.populateModel(applicationForm, model, bindingResult, applicantSection);

        OpenSectionViewModel viewModel = (OpenSectionViewModel) result;

        assertFalse(viewModel.getYourFinancesCompleteForAllOrganisations());

        verify(sectionService).getCompletedSectionsByOrganisation(application.getId());
        verify(sectionService).getSectionsForCompetitionByType(application.getCompetition(), FINANCE);
        verify(sectionService).getSectionsForCompetitionByType(application.getCompetition(), OVERVIEW_FINANCES);
    }

    @Test
    public void populateMode_nonCollaborativeProjectWhenCollaborationLevelIsSingle() {
        competition.setCollaborationLevel(CollaborationLevel.SINGLE);
        application.setCollaborativeProject(false);

        BaseSectionViewModel result = populator.populateModel(applicationForm, model, bindingResult, applicantSection);

        assertEquals(OpenSectionViewModel.class, result.getClass());
        OpenSectionViewModel viewModel = (OpenSectionViewModel) result;

        assertFalse(viewModel.isCollaborativeProject());
    }

    @Test
    public void populateMode_nonCollaborativeProjectWithSingleOrganisationWhenCollaborationIsSupported() {
        competition.setCollaborationLevel(CollaborationLevel.SINGLE_OR_COLLABORATIVE);
        application.setCollaborativeProject(false);

        BaseSectionViewModel result = populator.populateModel(applicationForm, model, bindingResult, applicantSection);

        assertEquals(OpenSectionViewModel.class, result.getClass());
        OpenSectionViewModel viewModel = (OpenSectionViewModel) result;

        assertFalse(viewModel.isCollaborativeProject());
    }

    @Test
    public void populateMode_nonCollaborativeProjectWithMultipleOrganisationsWhenCollaborationIsSupported() {
        competition.setCollaborationLevel(CollaborationLevel.SINGLE_OR_COLLABORATIVE);
        application.setCollaborativeProject(true);

        BaseSectionViewModel result = populator.populateModel(applicationForm, model, bindingResult, applicantSection);

        assertEquals(OpenSectionViewModel.class, result.getClass());
        OpenSectionViewModel viewModel = (OpenSectionViewModel) result;

        assertTrue(viewModel.isCollaborativeProject());
    }
}