package org.innovateuk.ifs.application.creation.controller;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.Validator;

import static com.google.common.primitives.Longs.asList;
import static org.innovateuk.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.creation.controller.ApplicationCreationAuthenticatedController.FORM_RADIO_NAME;
import static org.innovateuk.ifs.application.creation.controller.ApplicationCreationAuthenticatedController.RADIO_TRUE;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.resource.OrganisationTypeEnum.RTO;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ApplicationCreationAuthenticatedControllerTest extends BaseUnitTest {
    @InjectMocks
    private ApplicationCreationAuthenticatedController applicationCreationController;

    @Mock
    private Validator validator;

    private String COMPANY_ID = "08241216";
    private String COMPANY_NAME = "NETWORTHNET LTD";
    private OrganisationResource organisationResource;
    private ApplicationResource applicationResource;

    @Before
    public void setUp() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        mockMvc = setupMockMvc(applicationCreationController, () -> loggedInUser, env, messageSource);

        super.setup();

        applicationResource = newApplicationResource().withId(6L).withName("some application").build();
        when(applicationService.createApplication(anyLong(), anyLong(), anyString())).thenReturn(applicationResource);
        when(organisationService.getOrganisationForUser(loggedInUser.getId())).thenReturn(newOrganisationResource()
                .withId(5L)
                .withOrganisationType(RTO.getId())
                .withOrganisationTypeName(RTO.name())
                .withName(COMPANY_NAME).build());
        when(competitionService.getById(1L)).thenReturn(newCompetitionResource().withLeadApplicantType(asList(2L, 3L)).build());
        loginDefaultUser();
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
        ApplicationResource application = new ApplicationResource();
        application.setId(99L);

        when(applicationService.createApplication(anyLong(), anyLong(), eq(""))).thenReturn(application);
        when(userService.userHasApplicationForCompetition(loggedInUser.getId(), 1L)).thenReturn(false);

        mockMvc.perform(get("/application/create-authenticated/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/99/team"));

        // application needs to be created.
        verify(applicationService, atLeastOnce()).createApplication(anyLong(), anyLong(), eq(""));
        verify(userService).userHasApplicationForCompetition(loggedInUser.getId(), 1L);
    }



    @Test
    public void testPostEmptyForm() throws Exception {
        mockMvc.perform(post("/application/create-authenticated/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/create-authenticated/1"));
    }

    @Test
    public void testPostCreateNewApplication() throws Exception {
        ApplicationResource application = new ApplicationResource();
        application.setId(99L);
        when(applicationService.createApplication(anyLong(), anyLong(), eq(""))).thenReturn(application);

        mockMvc.perform(post("/application/create-authenticated/1").param(FORM_RADIO_NAME, RADIO_TRUE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/99/team"));

        // application needs to be created.
        verify(applicationService, atLeastOnce()).createApplication(anyLong(), anyLong(), eq(""));
    }

    @Test
    public void testPostNoNewApplication() throws Exception {
        // This should just redirect to the dashboard.
        mockMvc.perform(post("/application/create-authenticated/1").param(FORM_RADIO_NAME, ApplicationCreationAuthenticatedController.RADIO_FALSE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testGetCreateNewApplicationNotEligible() throws Exception {
        when(competitionService.getById(1L)).thenReturn(newCompetitionResource().withLeadApplicantType(asList(1L)).build());
        mockMvc.perform(get("/application/create-authenticated/1")
                .param(FORM_RADIO_NAME, ApplicationCreationAuthenticatedController.RADIO_FALSE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/create-authenticated/1/not-eligible"));
    }

    @Test
    public void testPostCreateNewApplicationNotEligible() throws Exception {
        when(competitionService.getById(1L)).thenReturn(newCompetitionResource().withLeadApplicantType(asList(1L)).build());
        mockMvc.perform(post("/application/create-authenticated/1")
                .param(FORM_RADIO_NAME, ApplicationCreationAuthenticatedController.RADIO_FALSE))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/application/create-authenticated/1/not-eligible"));
    }

    @Test
    public void testGetShowEligiblePage() throws Exception {
        mockMvc.perform(get("/application/create-authenticated/1/not-eligible")
                .param(FORM_RADIO_NAME, ApplicationCreationAuthenticatedController.RADIO_FALSE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/authenticated-not-eligible"));
    }
}
