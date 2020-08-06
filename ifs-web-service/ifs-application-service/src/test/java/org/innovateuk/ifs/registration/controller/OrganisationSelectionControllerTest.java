package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.controller.OrganisationSelectionController;
import org.innovateuk.ifs.organisation.populator.OrganisationSelectionViewModelPopulator;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationSelectionViewModel;
import org.innovateuk.ifs.registration.service.OrganisationJourneyEnd;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OrganisationSelectionControllerTest extends BaseControllerMockMVCTest<OrganisationSelectionController> {

    @Mock
    private RegistrationCookieService registrationCookieService;
    @Mock
    private OrganisationSelectionViewModelPopulator populator;
    @Mock
    private OrganisationRestService organisationRestService;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private OrganisationJourneyEnd organisationJourneyEnd;
    @Mock
    private InviteRestService inviteRestService;

    @Test
    public void viewPreviousOrganisations() throws Exception {
        OrganisationSelectionViewModel model = mock(OrganisationSelectionViewModel.class);

        when(populator.populate(eq(loggedInUser), any(), eq("/organisation/create/organisation-type"))).thenReturn(model);
        when(organisationRestService.getOrganisations(loggedInUser.getId(), false)).thenReturn(restSuccess(newOrganisationResource().build(1)));
        when(registrationCookieService.isCollaboratorJourney(any())).thenReturn(false);

        mockMvc.perform(get("/organisation/select"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/organisation/select-organisation"))
                .andExpect(model().attribute("model", model));

        verify(populator).populate(eq(loggedInUser), any(), eq("/organisation/create/organisation-type"));
    }

    @Test
    public void viewPreviousOrganisations_redirectIfNoAttachedOrganisations() throws Exception {
        when(organisationRestService.getOrganisations(loggedInUser.getId(), false)).thenReturn(restSuccess(emptyList()));

        mockMvc.perform(get("/organisation/select"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/create/organisation-type"));
    }

    @Test
    public void viewPreviousOrganisations_redirectIfNotApplicant() throws Exception {
        setLoggedInUser(newUserResource().withRolesGlobal(asList(Role.ASSESSOR)).build());

        mockMvc.perform(get("/organisation/select"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/create/organisation-type"));
    }

    @Test
    public void selectOrganisation_ineligible() throws Exception {
        long competitionId = 1L;
        long organisationId = 2L;

        when(registrationCookieService.isLeadJourney(any())).thenReturn(true);
        when(registrationCookieService.getCompetitionIdCookieValue(any())).thenReturn(Optional.of(competitionId));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(newOrganisationResource()
                .withOrganisationType(3L).build()));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(newCompetitionResource()
                .withLeadApplicantType(asList(4L, 5L)).build()));

        mockMvc.perform(post("/organisation/select")
                .param("selectedOrganisationId", String.valueOf(organisationId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/create/organisation-type/not-eligible"));
    }

    @Test
    public void selectOrganisation_collaborator() throws Exception {
        long organisationId = 2L;
        String view = "some-view";

        when(registrationCookieService.isLeadJourney(any())).thenReturn(false);
        when(organisationJourneyEnd.completeProcess(any(), any(), eq(loggedInUser), eq(organisationId))).thenReturn(view);
        mockMvc.perform(post("/organisation/select")
                .param("selectedOrganisationId", String.valueOf(organisationId)))
                .andExpect(status().isOk())
                .andExpect(view().name(view));

        verify(organisationJourneyEnd).completeProcess(any(), any(), eq(loggedInUser), eq(organisationId));
    }

    @Test
    public void selectOrganisation_collaborator_ineligible() throws Exception {
        long competitionId = 1L;
        long organisationId = 2L;
        String hash = "hash";
        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withLeadApplicantType(asList(5L))
                .build();
        ApplicationInviteResource applicationInviteResource = newApplicationInviteResource()
                .withCompetitionId(competitionId)
                .withHash(hash)
                .build();

        when(registrationCookieService.isLeadJourney(any())).thenReturn(false);
        when(registrationCookieService.isCollaboratorJourney(any())).thenReturn(true);
        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.of(hash));
        when(inviteRestService.getInviteByHash(any())).thenReturn(restSuccess(applicationInviteResource));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(organisationRestService.getOrganisationById(organisationId)).thenReturn(restSuccess(newOrganisationResource().withOrganisationType(5L).build()));

        mockMvc.perform(post("/organisation/select")
                .param("selectedOrganisationId", String.valueOf(organisationId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/create/organisation-type/not-eligible"));
    }

    @Override
    protected OrganisationSelectionController supplyControllerUnderTest() {
        return new OrganisationSelectionController();
    }
}