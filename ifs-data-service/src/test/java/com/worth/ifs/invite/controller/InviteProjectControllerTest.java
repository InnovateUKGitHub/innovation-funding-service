package com.worth.ifs.invite.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.invite.builder.ProjectInviteResourceBuilder;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.domain.ProjectInvite;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.invite.transactional.InviteProjectService;
import com.worth.ifs.project.resource.ProjectUserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static com.worth.ifs.invite.builder.InviteResourceBuilder.newInviteResource;
import static com.worth.ifs.invite.builder.ProjectInviteResourceBuilder.newInviteProjectResource;
import static com.worth.ifs.invite.builder.ProjectInviteResourceBuilder.newInviteProjectResources;

import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
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

    @Mock
    private InviteProjectService inviteProjectService;

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


//        mockMvc.perform(put("/projectinvite/save-finance-contact-invite")
//                .contentType(APPLICATION_JSON)
//                .content(toJson(inviteProjectResource)))
//                .andExpect(status().isOk());
//
//        verify(inviteProjectServiceMock).saveFinanceContactInvite(inviteProjectResource);


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

//        mockMvc.perform(get("/projectinvite/getInvitesByProjectId/{projectId}", 123L)).
//                andExpect(status().isOk()).
//                andExpect(content().json(toJson(inviteProjectResources)));
    }



}
