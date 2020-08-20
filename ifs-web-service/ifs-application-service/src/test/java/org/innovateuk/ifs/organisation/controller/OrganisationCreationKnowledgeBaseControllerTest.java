package org.innovateuk.ifs.organisation.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.registration.form.InviteAndIdCookie;
import org.innovateuk.ifs.registration.form.KnowledgeBaseForm;
import org.innovateuk.ifs.registration.form.OrganisationCreationForm;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.service.KnowledgeBaseRestService;
import org.innovateuk.ifs.user.service.OrganisationSearchRestService;
import org.innovateuk.ifs.user.service.OrganisationTypeRestService;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.organisation.controller.AbstractOrganisationCreationController.CONFIRM_ORGANISATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrganisationCreationKnowledgeBaseControllerTest extends BaseControllerMockMVCTest<OrganisationCreationKnowledgeBaseController> {

    static final String BASE_URL = "/organisation/create";

    @Mock
    private KnowledgeBaseRestService knowledgeBaseRestService;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private OrganisationTypeRestService organisationTypeRestService;

    @Mock
    private OrganisationSearchRestService organisationSearchRestService;

    protected OrganisationCreationKnowledgeBaseController supplyControllerUnderTest() {
        return new OrganisationCreationKnowledgeBaseController();
    }

    @Test
    public void selectKnowledgeBase() throws Exception {
        InviteAndIdCookie projectInviteCookie = new InviteAndIdCookie(1L, "hash");
        when(registrationCookieService.getProjectInviteHashCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(projectInviteCookie));
        when(knowledgeBaseRestService.getKnowledgeBases()).thenReturn(restSuccess(singletonList("KnowledgeBase1")));

        mockMvc.perform(get(BASE_URL + "/knowledge-base"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/organisation/knowledge-base"));
    }

    @Test
    public void selectedKnowledgeBase() throws Exception {
        KnowledgeBaseForm knowledgeBaseForm = new KnowledgeBaseForm();
        knowledgeBaseForm.setKnowledgeBase("KnowledgeBase 1");

        OrganisationCreationForm organisationCreationForm = new OrganisationCreationForm();
        organisationCreationForm.setOrganisationName(knowledgeBaseForm.getKnowledgeBase());
        organisationCreationForm.setOrganisationSearchName(knowledgeBaseForm.getKnowledgeBase());
        organisationCreationForm.setOrganisationTypeId(5L);

        when(registrationCookieService.getOrganisationCreationCookieValue(any(HttpServletRequest.class))).thenReturn(Optional.of(organisationCreationForm));
        when(knowledgeBaseRestService.getKnowledgeBases()).thenReturn(restSuccess(singletonList(organisationCreationForm.getOrganisationName())));

        mockMvc.perform(post(BASE_URL + "/knowledge-base")
                .param("knowledgeBase", "KnowledgeBase 1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL + "/" + CONFIRM_ORGANISATION));
    }
}