package org.innovateuk.ifs.project.grants.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.grants.service.GrantsInviteRestService;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole;
import org.innovateuk.ifs.project.grants.viewmodel.GrantsInviteSendViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource.GrantsInviteRole.GRANTS_PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder.newPartnerOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GrantsInviteControllerTest extends BaseControllerMockMVCTest<GrantsInviteController> {

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private GrantsInviteRestService grantsInviteRestService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Test
    public void inviteForm() throws Exception {
        ProjectResource project = newProjectResource()
                .withApplication(2L)
                .withName("name")
                .build();
        List<PartnerOrganisationResource> partners = newPartnerOrganisationResource()
                .withOrganisation(5L, 6L)
                .withOrganisationName("5", "6")
                .build(2);

        when(projectRestService.getProjectById(project.getId())).thenReturn(restSuccess(project));
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId())).thenReturn(restSuccess(partners));

        MvcResult result = mockMvc.perform(get("/project/" + project.getId()+ "/grants/invite/send"))
                .andExpect(view().name("project/grants-invite/invite"))
                .andReturn();

        GrantsInviteSendViewModel viewModel = (GrantsInviteSendViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(viewModel.getApplicationId(), 2L);
        assertEquals(viewModel.getProjectName(), "name");
        assertEquals(viewModel.getOrganisationNameIdPairs().get(0).getLeft(), partners.get(0).getOrganisation());
        assertEquals(viewModel.getOrganisationNameIdPairs().get(0).getRight(), partners.get(0).getOrganisationName());
        assertEquals(viewModel.getOrganisationNameIdPairs().get(1).getLeft(), partners.get(1).getOrganisation());
        assertEquals(viewModel.getOrganisationNameIdPairs().get(1).getRight(), partners.get(1).getOrganisationName());
    }

    @Test
    public void sentInvite_validation() throws Exception {
        ProjectResource project = newProjectResource()
                .withApplication(2L)
                .withName("name")
                .build();
        List<PartnerOrganisationResource> partners = newPartnerOrganisationResource().build(2);

        when(projectRestService.getProjectById(project.getId())).thenReturn(restSuccess(project));
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(project.getId())).thenReturn(restSuccess(partners));

        mockMvc.perform(post("/project/" + project.getId()+ "/grants/invite/send")
                .param("role", GRANTS_PROJECT_FINANCE_CONTACT.name()))
                .andExpect(view().name("project/grants-invite/invite"))
                .andExpect(model().attributeHasFieldErrorCode("form", "firstName", "NotBlank"))
                .andExpect(model().attributeHasFieldErrorCode("form", "lastName", "NotBlank"))
                .andExpect(model().attributeHasFieldErrorCode("form", "email", "NotBlank"))
                .andExpect(model().attributeHasFieldErrorCode("form", "organisationId", "validation.grants.invite.organisation.required"));
    }

    @Test
    public void sentInvite_success() throws Exception {
        long projectId = 1L;
        String firstName = "Bob";
        String lastName = "Bobel";
        String email = "bob.bobel@bobbins.com";
        GrantsInviteRole role = GrantsInviteRole.GRANTS_PROJECT_MANAGER;

        when(grantsInviteRestService.invite(projectId, new GrantsInviteResource(firstName + " " + lastName, email, role))).thenReturn(restSuccess());

        mockMvc.perform(post("/project/" + projectId + "/grants/invite/send")
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("email", email)
                .param("role", role.name()))
                .andExpect(redirectedUrl(String.format("/project/%d/grants/invite", projectId)));

        verify(grantsInviteRestService).invite(projectId, new GrantsInviteResource(firstName + " " + lastName, email, role));
    }

    @Override
    protected GrantsInviteController supplyControllerUnderTest() {
        return new GrantsInviteController();
    }
}
