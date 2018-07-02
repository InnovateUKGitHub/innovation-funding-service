package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.registration.controller.OrganisationCreationLeadTypeController;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.populator.OrganisationCreationSelectTypePopulator;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.registration.viewmodel.OrganisationCreationSelectTypeViewModel;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.innovateuk.ifs.CookieTestUtil.setupCookieUtil;
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
    private CookieUtil cookieUtil;

    @Mock
    private CompetitionService competitionService;

    private OrganisationTypeForm organisationTypeForm;

    private OrganisationCreationForm organisationForm;

    @Before
    public void setup(){
        super.setup();
        setupCookieUtil(cookieUtil);

        when(registrationCookieService.getCompetitionIdCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(1L));
        when(registrationCookieService.getOrganisationTypeCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.empty());
        when(competitionService.getOrganisationTypes(1L)).thenReturn(newOrganisationTypeResource().withId(1L, 3L).build(2));
        when(organisationCreationSelectTypePopulator.populate()).thenReturn(new OrganisationCreationSelectTypeViewModel(newOrganisationTypeResource().build(4)));
    }

    @Test
    public void testSelectedLeadOrganisationTypeEligible() throws Exception {
        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .param("organisationTypeId", OrganisationTypeEnum.RTO.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/find-organisation"));
    }

    @Test
    public void testSelectedLeadOrganisationTypeNotEligible() throws Exception {
        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .param("organisationTypeId", OrganisationTypeEnum.RESEARCH.getId().toString()))
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