package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.form.OrganisationTypeForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.user.resource.OrganisationTypeResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.user.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrganisationCreationLeadTypeControllerTest extends BaseControllerMockMVCTest<OrganisationCreationLeadTypeController> {

    protected OrganisationCreationLeadTypeController supplyControllerUnderTest() {
        return new OrganisationCreationLeadTypeController();
    }

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private CompetitionService competitionService;

    private OrganisationTypeForm organisationTypeForm;
    private OrganisationCreationForm organisationForm;

    @Before
    public void setup(){
        super.setup();

        when(registrationCookieService.getCompetitionIdCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(1L));
        when(competitionService.getPublishedById(1L)).thenReturn(newCompetitionResource().withId(1L).build());
    }

    @Test
    public void testSelectedBusinessSaveLeadBusiness() throws Exception {
        when(registrationCookieService.getOrganisationTypeCookieValue(any())).thenReturn(Optional.ofNullable(organisationTypeForm));
        when(registrationCookieService.getOrganisationCreationCookieValue(any())).thenReturn(Optional.ofNullable(organisationForm));

        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .param("organisationTypeId", OrganisationTypeEnum.RTO.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }

    @Test
    public void testSelectedInvalidLeadOrganisationType() throws Exception {
        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .param("organisationTypeId", OrganisationTypeEnum.RESEARCH.getId().toString()))
                .andExpect(view().name("registration/organisation/lead-organisation-type"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("organisationFormCookie", "organisationTypeId"));
    }

    @Test
    public void testLeadOrganisationTypeNotSelected() throws Exception {
        mockMvc.perform(post("/organisation/create/lead-organisation-type"))
                .andExpect(view().name("registration/organisation/lead-organisation-type"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("organisationFormCookie", "organisationTypeId"));
    }

    @Test
    public void testSelectOrganisationType_leadApplicantShouldRedirectToConfirmPageWhenOnlyOneOrganisationTypeIsAllowed() throws Exception {
        Long competitionId = 2L;
        Cookie competitionIdCookie = new Cookie("competitionId", encryptor.encrypt(competitionId.toString()));
        List<OrganisationTypeResource> organisationTypeResourceList = newOrganisationTypeResource().withId(OrganisationTypeEnum.BUSINESS.getId()).build(1);

        when(competitionService.getOrganisationTypes(competitionId)).thenReturn(organisationTypeResourceList);

        mockMvc.perform(get("/organisation/create/lead-organisation-type")
                .cookie(competitionIdCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }

    @Test
    public void testConfirmSelectOrganisationType_leadApplicantShouldRedirectToConfirmPageWhenOnlyOneOrganisationTypeIsAllowed() throws Exception {
        Long competitionId = 2L;
        Cookie competitionIdCookie = new Cookie("competitionId", encryptor.encrypt(competitionId.toString()));
        List<OrganisationTypeResource> organisationTypeResourceList = newOrganisationTypeResource().withId(OrganisationTypeEnum.BUSINESS.getId()).build(1);

        when(competitionService.getOrganisationTypes(competitionId)).thenReturn(organisationTypeResourceList);

        mockMvc.perform(post("/organisation/create/lead-organisation-type")
                .cookie(competitionIdCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/organisation/create/confirm-organisation"));
    }
}