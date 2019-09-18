package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.registration.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.registration.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.util.EncryptedCookieService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static java.lang.String.valueOf;
import static org.innovateuk.ifs.util.CookieTestUtil.setupEncryptedCookieService;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrganisationCreationLeadTypeControllerTest extends BaseControllerMockMVCTest<OrganisationCreationLeadTypeController> {

    protected OrganisationCreationLeadTypeController supplyControllerUnderTest() {
        return new OrganisationCreationLeadTypeController();
    }

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private OrganisationCreationSelectTypePopulator organisationCreationSelectTypePopulator;

    @Mock
    private EncryptedCookieService cookieUtil;

    @Mock
    private CompetitionRestService competitionRestService;


    @Before
    public void setup(){
        super.setup();
        setupEncryptedCookieService(cookieUtil);

        when(registrationCookieService.getCompetitionIdCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(1L));
        when(registrationCookieService.getOrganisationTypeCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.empty());
        when(competitionRestService.getCompetitionOrganisationType(1L)).thenReturn(restSuccess(newOrganisationTypeResource().withId(1L, 3L).build(2)));
        when(organisationCreationSelectTypePopulator.populate()).thenReturn(new OrganisationCreationSelectTypeViewModel(newOrganisationTypeResource().build(4)));
    }

    @Test
    public void testSelectedLeadOrganisationTypeEligible() throws Exception {
        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .param("organisationTypeId", valueOf(OrganisationTypeEnum.RTO.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"));
    }

    @Test
    public void testSelectedLeadOrganisationTypeNotEligible() throws Exception {
        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .param("organisationTypeId", valueOf(OrganisationTypeEnum.RESEARCH.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/lead-organisation-type/not-eligible"));
    }

    @Test
    public void testLeadOrganisationTypeNotSelected() throws Exception {
        mockMvc.perform(post("/organisation/create/lead-organisation-type"))
                .andExpect(view().name("registration/organisation/lead-organisation-type"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("organisationForm", "organisationTypeId"));
    }

    @Test
    public void testLeadOrganisationTypeIncorrectSelected() throws Exception {
        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .param("organisationTypeId", "300"))
                .andExpect(view().name("registration/organisation/lead-organisation-type"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("organisationForm", "organisationTypeId"));
    }
}