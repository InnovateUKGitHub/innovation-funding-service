package org.innovateuk.ifs.application.creation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.application.service.QuestionRestService;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.Validator;

import static com.google.common.primitives.Longs.asList;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.APPLICATION_TEAM;
import static org.innovateuk.ifs.form.builder.QuestionResourceBuilder.newQuestionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum.RTO;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationCreationAuthenticatedControllerTest extends BaseControllerMockMVCTest<ApplicationCreationAuthenticatedController> {

    @Mock
    private Validator validator;

    private String COMPANY_NAME = "NETWORTHNET LTD";
    private OrganisationResource organisationResource;
    private ApplicationResource applicationResource;

    private UserResource loggedInUser = newUserResource().withId(1L)
            .withFirstName("James")
            .withLastName("Watts")
            .withEmail("james.watts@email.co.uk")
            .withRolesGlobal(singletonList(Role.APPLICANT))
            .withUID("2aerg234-aegaeb-23aer").build();

    @Mock
    private ApplicationService applicationService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private UserService userService;

    @Mock
    private QuestionRestService questionRestService;

    @Override
    protected ApplicationCreationAuthenticatedController supplyControllerUnderTest() {
        return new ApplicationCreationAuthenticatedController();
    }

    @Before
    public void setUp() {
        super.setUp();

        applicationResource = newApplicationResource().withId(6L).withName("some application").build();
        when(applicationService.createApplication(anyLong(), anyLong(), anyString())).thenReturn(applicationResource);
        when(organisationService.getOrganisationForUser(loggedInUser.getId())).thenReturn(newOrganisationResource()
                .withId(5L)
                .withOrganisationType(RTO.getId())
                .withOrganisationTypeName(RTO.name())
                .withName(COMPANY_NAME).build());
        when(competitionService.getById(1L)).thenReturn(newCompetitionResource().withLeadApplicantType(asList(2L, 3L)).build());
    }

    @Test
    public void testGetRequestWithExistingApplication() throws Exception {
        when(userService.userHasApplicationForCompetition(loggedInUser.getId(), 1L)).thenReturn(true);
        mockMvc.perform(get("/application/create-authenticated/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/confirm-new-application"));
        verify(userService).userHasApplicationForCompetition(loggedInUser.getId(), 1L);
    }

    @Test
    public void testGetRequestWithoutExistingApplication() throws Exception {
        long competitionId = 1L;
        ApplicationResource application = newApplicationResource().build();
        QuestionResource applicationTeamQuestion = newQuestionResource().build();

        when(applicationService.createApplication(competitionId, loggedInUser.getId(), "")).thenReturn(application);
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, APPLICATION_TEAM))
                .thenReturn(restSuccess(applicationTeamQuestion));
        when(userService.userHasApplicationForCompetition(loggedInUser.getId(), 1L)).thenReturn(false);

        mockMvc.perform(get("/application/create-authenticated/{competitionId}", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/form/question/%s", application.getId(),
                        applicationTeamQuestion.getId())));

        verify(applicationService, only()).createApplication(competitionId, loggedInUser.getId(), "");
        verify(questionRestService, only()).getQuestionByCompetitionIdAndQuestionSetupType(competitionId, APPLICATION_TEAM);
        verify(userService, only()).userHasApplicationForCompetition(loggedInUser.getId(), competitionId);
    }

    @Test
    public void testGetRequestWithoutExistingApplicationAndWithOldApplicantMenu() throws Exception {
        long competitionId = 1L;
        ApplicationResource application = newApplicationResource().build();

        when(applicationService.createApplication(competitionId, loggedInUser.getId(), "")).thenReturn(application);
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId,
                APPLICATION_TEAM))
                .thenReturn(restFailure(notFoundError(QuestionResource.class, competitionId, APPLICATION_TEAM)));
        when(userService.userHasApplicationForCompetition(loggedInUser.getId(), 1L)).thenReturn(false);

        mockMvc.perform(get("/application/create-authenticated/{competitionId}", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", application.getId())));

        verify(applicationService, only()).createApplication(competitionId, loggedInUser.getId(), "");
        verify(questionRestService, only()).getQuestionByCompetitionIdAndQuestionSetupType(competitionId, APPLICATION_TEAM);
        verify(userService, only()).userHasApplicationForCompetition(loggedInUser.getId(), competitionId);
    }

    @Test
    public void testPostEmptyFormShouldThrowError() throws Exception {
        mockMvc.perform(post("/application/create-authenticated/1"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "createNewApplication"))
                .andReturn();
    }

    @Test
    public void testPostCreateNewApplication() throws Exception {
        long competitionId = 1L;
        ApplicationResource application = newApplicationResource().build();
        QuestionResource applicationTeamQuestion = newQuestionResource().build();

        when(applicationService.createApplication(competitionId, loggedInUser.getId(), "")).thenReturn(application);
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId, APPLICATION_TEAM))
                .thenReturn(restSuccess(applicationTeamQuestion));

        mockMvc.perform(post("/application/create-authenticated/{competitionId}", competitionId)
                .param("createNewApplication", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/form/question/%s", application.getId(),
                        applicationTeamQuestion.getId())));

        verify(applicationService, only()).createApplication(competitionId, loggedInUser.getId(), "");
        verify(questionRestService, only()).getQuestionByCompetitionIdAndQuestionSetupType(competitionId, APPLICATION_TEAM);
    }

    @Test
    public void testPostCreateNewApplicationAndWithOldApplicantMenu() throws Exception {
        long competitionId = 1L;
        ApplicationResource application = newApplicationResource().build();

        when(applicationService.createApplication(competitionId, loggedInUser.getId(), "")).thenReturn(application);
        when(questionRestService.getQuestionByCompetitionIdAndQuestionSetupType(competitionId,
                APPLICATION_TEAM))
                .thenReturn(restFailure(notFoundError(QuestionResource.class, competitionId, APPLICATION_TEAM)));

        mockMvc.perform(post("/application/create-authenticated/{competitionId}", competitionId)
                .param("createNewApplication", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/application/%s/team", application.getId())));

        verify(applicationService, only()).createApplication(competitionId, loggedInUser.getId(), "");
        verify(questionRestService, only()).getQuestionByCompetitionIdAndQuestionSetupType(competitionId, APPLICATION_TEAM);
    }

    @Test
    public void testPostNoNewApplication() throws Exception {
        // This should just redirect to the dashboard.
        mockMvc.perform(post("/application/create-authenticated/1").param("createNewApplication", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testGetCreateNewApplicationNotEligible() throws Exception {
        when(competitionService.getById(1L)).thenReturn(newCompetitionResource().withLeadApplicantType(asList(1L)).build());
        mockMvc.perform(get("/application/create-authenticated/1")
                .param("createNewApplication", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/create-authenticated/1/not-eligible"));
    }

    @Test
    public void testPostCreateNewApplicationNotEligible() throws Exception {
        when(competitionService.getById(1L)).thenReturn(newCompetitionResource().withLeadApplicantType(asList(1L)).build());
        mockMvc.perform(post("/application/create-authenticated/1")
                .param("createNewApplication", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/create-authenticated/1/not-eligible"));
    }

    @Test
    public void testGetShowEligiblePage() throws Exception {
        mockMvc.perform(get("/application/create-authenticated/1/not-eligible")
                .param("createNewApplication", "0"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/authenticated-not-eligible"));
    }
}
