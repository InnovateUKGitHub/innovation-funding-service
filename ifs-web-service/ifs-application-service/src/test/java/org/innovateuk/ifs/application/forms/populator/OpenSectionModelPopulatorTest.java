package org.innovateuk.ifs.application.forms.populator;

import org.innovateuk.ifs.application.populator.ApplicationSectionAndQuestionModelPopulator;
import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.applicant.resource.ApplicantSectionResource;
import org.innovateuk.ifs.application.populator.ApplicationNavigationPopulator;
import org.innovateuk.ifs.application.populator.OpenSectionModelPopulator;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.application.viewmodel.BaseSectionViewModel;
import org.innovateuk.ifs.application.viewmodel.OpenSectionViewModel;
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
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
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
import static org.innovateuk.ifs.applicant.builder.ApplicantResourceBuilder.newApplicantResource;
import static org.innovateuk.ifs.applicant.builder.ApplicantSectionResourceBuilder.newApplicantSectionResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.form.builder.FormInputResourceBuilder.newFormInputResource;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.FormInputScope.APPLICATION;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.assertEquals;
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

        Long competitionId = 1L, applicationId = 23L;
        Long organisationId = 245L;

        application = newApplicationResource().withId(applicationId).withCompetition(competitionId).build();

        applicationForm = new ApplicationForm();
        applicationForm.setApplication(application);
        applicationForm.setStateAidAgreed(Boolean.TRUE);
        applicationForm.setTermsAgreed(Boolean.TRUE);

        List<Long> questionResourceList = asList(34L, 35L);
        section = newSectionResource()
                .withChildSections(Collections.emptyList())
                .withQuestions(questionResourceList)
                .withCompetition(competitionId)
                .withType(SectionType.FINANCE).build();
        user = newUserResource().build();
        competition = newCompetitionResource().withId(competitionId).build();
        List<FormInputResource> formInputs = newFormInputResource().withQuestion(section.getQuestions().get(0)).build(2);

        ApplicantResource applicant = newApplicantResource()
                .withProcessRole(newProcessRoleResource()
                        .withUser(user)
                        .withRoleName("leadapplicant")
                        .build())
                .withOrganisation(newOrganisationResource()
                        .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId())
                        .withId(organisationId)
                        .build())
                .build();
        applicantSection = newApplicantSectionResource()
                .withApplication(application)
                .withCompetition(competition)
                .withCurrentApplicant(applicant)
                .withApplicants(asList(applicant))
                .withSection(section).withCurrentUser(user).build();

        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userRestService.retrieveUserById(user.getId())).thenReturn(restSuccess(user));

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

        when(userService.getUserOrganisationId(user.getId(), application.getId())).thenReturn(organisationId);

        List<FormInputResponseResource> formInputResponseResources = new ArrayList<>();
        when(formInputResponseRestService.getResponsesByApplicationId(application.getId())).thenReturn(restSuccess(formInputResponseResources));
        when(formInputResponseService.mapFormInputResponsesToFormInput(formInputResponseResources)).thenReturn(new HashMap<>());
    }

    @Test
    public void testPopulateModelWithValidObjects() throws Exception {

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
    }

    @Test
    public void testYourFinancesCompleteForAllOrganisations() {
        List<SectionResource> eachOrganisationFinanceSections = newSectionResource().build(1);
        List<OrganisationResource> organisations = newOrganisationResource().build(3);

        Map<Long, Set<Long>> completedSectionsByOrganisations = new HashMap<>();
        completedSectionsByOrganisations.put(organisations.get(0).getId(), asLinkedSet(eachOrganisationFinanceSections.get(0).getId()));
        completedSectionsByOrganisations.put(organisations.get(1).getId(), asLinkedSet(eachOrganisationFinanceSections.get(0).getId()));
        completedSectionsByOrganisations.put(organisations.get(2).getId(), new HashSet<>());

        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(completedSectionsByOrganisations);
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE)).thenReturn(eachOrganisationFinanceSections);

        BaseSectionViewModel result = populator.populateModel(applicationForm, model, bindingResult, applicantSection);

        OpenSectionViewModel viewModel = (OpenSectionViewModel) result;

        assertEquals(application, viewModel.getApplication().getCurrentApplication());
        assertEquals(false, viewModel.getYourFinancesCompleteForAllOrganisations());
    }

    @Test
    public void testYourFinancesInCompleteForAnOrganisations() {
        List<SectionResource> eachOrganisationFinanceSections = newSectionResource().build(1);
        List<OrganisationResource> organisations = newOrganisationResource().build(3);

        Map<Long, Set<Long>> completedSectionsByOrganisations = new HashMap<>();
        completedSectionsByOrganisations.put(organisations.get(0).getId(), asLinkedSet(eachOrganisationFinanceSections.get(0).getId()));
        completedSectionsByOrganisations.put(organisations.get(1).getId(), asLinkedSet(eachOrganisationFinanceSections.get(0).getId()));
        completedSectionsByOrganisations.put(organisations.get(2).getId(), asLinkedSet(eachOrganisationFinanceSections.get(0).getId()));

        when(sectionService.getCompletedSectionsByOrganisation(application.getId())).thenReturn(completedSectionsByOrganisations);
        when(sectionService.getSectionsForCompetitionByType(application.getCompetition(), SectionType.FINANCE)).thenReturn(eachOrganisationFinanceSections);

        BaseSectionViewModel result = populator.populateModel(applicationForm, model, bindingResult, applicantSection);

        OpenSectionViewModel viewModel = (OpenSectionViewModel) result;

        assertEquals(application, viewModel.getApplication().getCurrentApplication());
        assertEquals(true, viewModel.getYourFinancesCompleteForAllOrganisations());
    }
}