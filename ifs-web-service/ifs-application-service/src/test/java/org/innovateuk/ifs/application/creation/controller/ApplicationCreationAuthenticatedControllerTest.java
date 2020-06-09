package org.innovateuk.ifs.application.creation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationCreationAuthenticatedControllerTest extends BaseControllerMockMVCTest<ApplicationCreationAuthenticatedController> {

    @Mock
    private UserService userService;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Override
    protected ApplicationCreationAuthenticatedController supplyControllerUnderTest() {
        return new ApplicationCreationAuthenticatedController();
    }

    @Test
    public void testGetRequestWithExistingApplication() throws Exception {
        when(userService.userHasApplicationForCompetition(loggedInUser.getId(), 1L)).thenReturn(true);
        mockMvc.perform(get("/application/create-authenticated/{competitionId}", 1L))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("create-application/confirm-new-application"));
        verify(userService).userHasApplicationForCompetition(loggedInUser.getId(), 1L);
    }

    @Test
    public void testGetRequestWithoutExistingApplication() throws Exception {
        when(userService.userHasApplicationForCompetition(loggedInUser.getId(), 1L)).thenReturn(false);
        when(competitionOrganisationConfigRestService.findByCompetitionId(1L)).thenReturn(RestResult.restSuccess(new CompetitionOrganisationConfigResource(false, false)));
        mockMvc.perform(get("/application/create-authenticated/{competitionId}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/select"));

        verify(registrationCookieService).deleteAllRegistrationJourneyCookies(any());
        verify(registrationCookieService).saveToCompetitionIdCookie(eq(1L), any());
        verify(userService, only()).userHasApplicationForCompetition(loggedInUser.getId(), 1L);
    }

    @Test
    public void testPostEmptyFormShouldThrowError() throws Exception {
        when(competitionOrganisationConfigRestService.findByCompetitionId(1L)).thenReturn(RestResult.restSuccess(new CompetitionOrganisationConfigResource(false, false)));
        mockMvc.perform(post("/application/create-authenticated/1"))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("form", "createNewApplication"))
                .andReturn();
    }

    @Test
    public void testPostCreateNewApplication() throws Exception {
        when(competitionOrganisationConfigRestService.findByCompetitionId(1L)).thenReturn(RestResult.restSuccess(new CompetitionOrganisationConfigResource(false, false)));
        long competitionId = 1L;
        mockMvc.perform(post("/application/create-authenticated/{competitionId}", competitionId)
                .param("createNewApplication", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/select"));

        verify(registrationCookieService).deleteAllRegistrationJourneyCookies(any());
        verify(registrationCookieService).saveToCompetitionIdCookie(eq(1L), any());
    }

    @Test
    public void testPostNoNewApplication() throws Exception {
        when(competitionOrganisationConfigRestService.findByCompetitionId(1L)).thenReturn(RestResult.restSuccess(new CompetitionOrganisationConfigResource(false, false)));
        // This should just redirect to the dashboard.
        mockMvc.perform(post("/application/create-authenticated/1").param("createNewApplication", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testPostInternationalOrganisationApplication() throws Exception {
        when(competitionOrganisationConfigRestService.findByCompetitionId(1L)).thenReturn(RestResult.restSuccess(new CompetitionOrganisationConfigResource(true, true)));
        long competitionId = 1L;
        // This should just redirect to create international organisation.
        mockMvc.perform(post("/application/create-authenticated/{competitionId}", competitionId)
                .param("createNewApplication", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/create/international-organisation"));
    }
}