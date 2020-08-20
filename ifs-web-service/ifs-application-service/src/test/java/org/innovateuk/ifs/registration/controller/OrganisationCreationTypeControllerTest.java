package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.organisation.controller.OrganisationCreationTypeController;
import org.innovateuk.ifs.organisation.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.organisation.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static java.lang.String.valueOf;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.innovateuk.ifs.util.CookieTestUtil.setupEncryptedCookieService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrganisationCreationTypeControllerTest extends BaseControllerMockMVCTest<OrganisationCreationTypeController> {

    @Mock
    private RegistrationCookieService registrationCookieService;
    @Mock
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;
    @Mock
    private EncryptedCookieService cookieUtil;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;
    @Mock
    private InviteRestService inviteRestService;

    protected OrganisationCreationTypeController supplyControllerUnderTest() {
        return new OrganisationCreationTypeController();
    }

    @Before
    public void setup(){
        super.setup();
        setupEncryptedCookieService(cookieUtil);
        HttpServletRequest request = mock(HttpServletRequest.class);

        CompetitionOrganisationConfigResource competitionOrganisationConfigResource = new CompetitionOrganisationConfigResource();
        competitionOrganisationConfigResource.setInternationalOrganisationsAllowed(true);
        competitionOrganisationConfigResource.setInternationalLeadOrganisationAllowed(true);

        CompetitionResource competitionResource = newCompetitionResource().build();
        ApplicationInviteResource applicationInviteResource = newApplicationInviteResource().withCompetitionId(1L).build();

        when(registrationCookieService.getCompetitionIdCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(1L));
        when(registrationCookieService.getOrganisationTypeCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.empty());
        when(registrationCookieService.isLeadJourney(any(HttpServletRequest.class))).thenReturn(false);
        when(competitionRestService.getCompetitionOrganisationType(1L)).thenReturn(restSuccess(newOrganisationTypeResource().withId(1L, 3L).build(2)));
        when(organisationCreationSelectTypePopulator.populate(request, newCompetitionResource().build())).thenReturn(new OrganisationCreationSelectTypeViewModel(newOrganisationTypeResource().build(4), false));
        when(competitionOrganisationConfigRestService.findByCompetitionId(1L)).thenReturn(restSuccess(competitionOrganisationConfigResource));
        when(competitionRestService.getPublishedCompetitionById(1L)).thenReturn(restSuccess(competitionResource));
        when(registrationCookieService.getInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of("hash"));
        when(inviteRestService.getInviteByHash("hash")).thenReturn(restSuccess(applicationInviteResource));
    }

    @Test
    public void testSelectedLeadOrganisationTypeEligible() throws Exception {
        when(registrationCookieService.getCompetitionIdCookieValue(any())).thenReturn(Optional.of(1L));

        mockMvc.perform(post("/organisation/create/organisation-type")
                .param("organisationTypeId", valueOf(OrganisationTypeEnum.RTO.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"));
    }

    @Test
    public void testSelectedLeadOrganisationTypeNotEligible() throws Exception {
        when(registrationCookieService.getCompetitionIdCookieValue(any())).thenReturn(Optional.of(1L));
        when(registrationCookieService.isLeadJourney(any())).thenReturn(true);

        mockMvc.perform(post("/organisation/create/organisation-type")
                .param("organisationTypeId", valueOf(OrganisationTypeEnum.RESEARCH.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/organisation-type/not-eligible"));
    }

    @Test
    public void testLeadOrganisationTypeNotSelected() throws Exception {
        mockMvc.perform(post("/organisation/create/organisation-type"))
                .andExpect(view().name("registration/organisation/organisation-type"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("organisationForm", "organisationTypeId"));
    }

    @Test
    public void testLeadOrganisationTypeIncorrectSelected() throws Exception {
        mockMvc.perform(post("/organisation/create/organisation-type")
                .param("organisationTypeId", "300"))
                .andExpect(view().name("registration/organisation/organisation-type"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("organisationForm", "organisationTypeId"));
    }
}