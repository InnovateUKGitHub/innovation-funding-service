package org.innovateuk.ifs.application.creation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
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
        mockMvc.perform(get("/application/create-authenticated/{competitionId}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/select"));

        verify(registrationCookieService).deleteAllRegistrationJourneyCookies(any());
        verify(registrationCookieService).saveToCompetitionIdCookie(eq(1L), any());
        verify(userService, only()).userHasApplicationForCompetition(loggedInUser.getId(), 1L);
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
        mockMvc.perform(post("/application/create-authenticated/{competitionId}", competitionId)
                .param("createNewApplication", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/select"));

        verify(registrationCookieService).deleteAllRegistrationJourneyCookies(any());
        verify(registrationCookieService).saveToCompetitionIdCookie(eq(1L), any());
    }

    @Test
    public void testPostNoNewApplication() throws Exception {
        // This should just redirect to the dashboard.
        mockMvc.perform(post("/application/create-authenticated/1").param("createNewApplication", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}