package com.worth.ifs.invite.controller;



import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.builder.ProjectInviteResourceBuilder;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.invite.transactional.InviteProjectService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.util.JsonMappingUtil.fromJson;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InviteProjectControllerTest  extends BaseControllerMockMVCTest<InviteProjectController> {

    @Override
    protected InviteProjectController supplyControllerUnderTest() {
        return new InviteProjectController();
    }

    private InviteProjectResource inviteProjectResource;

    @Before
    public void setUp() {

        inviteProjectResource = ProjectInviteResourceBuilder.newInviteProjectResource().
                withId(1L).
                withEmail("testProject-invite@mail.com").
                withName("test-project-invitece").
                withStatus(InviteStatusConstants.CREATED).
                withOrganisation(25L).
                withProject(2L).
                build();
    }

    @Test
    public void saveProjectInvite() throws Exception {


        when(inviteProjectServiceMock.saveFinanceContactInvite(inviteProjectResource)).thenReturn(serviceSuccess());


        mockMvc.perform(put("/projectinvite/save-finance-contact-invite")
                .contentType(APPLICATION_JSON)
                .content(toJson(inviteProjectResource)))
                .andExpect(status().isOk());

        verify(inviteProjectServiceMock).saveFinanceContactInvite(inviteProjectResource);


    }

    @Test
    public void getProjectInvitesById() throws Exception {

        List<InviteProjectResource> inviteProjectResources = ProjectInviteResourceBuilder.newInviteProjectResources().
                withIds(1L).
                withEmails("testProject-invite@mail.com").
                withNames("test-project-invitece").
                withStatuss(InviteStatusConstants.CREATED).
                withOrganisations(25L).
                withProjects(2L).
                build(5);


        when(inviteProjectServiceMock.getInvitesByProject(123L)).thenReturn(serviceSuccess(inviteProjectResources));

        mockMvc.perform(get("/projectinvite/getInvitesByProjectId/{projectId}", 123L)).
                andExpect(status().isOk()).
                andExpect(content().json(toJson(inviteProjectResources)));

    }

    @Test
    public void acceptProjectInvite() throws Exception {


        when(inviteProjectServiceMock.acceptProjectInvite("has545967h",123L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/projectinvite/acceptInvite/{hash}/{userId}", "has545967h",123L)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(inviteProjectServiceMock).acceptProjectInvite("has545967h", 123L);


    }


}
