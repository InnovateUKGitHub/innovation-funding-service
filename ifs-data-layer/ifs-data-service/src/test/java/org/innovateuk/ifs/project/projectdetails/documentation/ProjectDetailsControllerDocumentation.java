package org.innovateuk.ifs.project.projectdetails.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.address.resource.PostcodeAndTownResource;
import org.innovateuk.ifs.invite.resource.ProjectUserInviteResource;
import org.innovateuk.ifs.project.projectdetails.controller.ProjectDetailsController;
import org.innovateuk.ifs.project.projectdetails.transactional.ProjectDetailsService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDate;

import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteResourceBuilder.newProjectUserInviteResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectDetailsControllerDocumentation extends BaseControllerMockMVCTest<ProjectDetailsController> {

    @Mock
    private ProjectDetailsService projectDetailsServiceMock;

    @Override
    protected ProjectDetailsController supplyControllerUnderTest() {
        return new ProjectDetailsController();
    }

    @Test
    public void updateStartDate() throws Exception {

        when(projectDetailsServiceMock.updateProjectStartDate(123L, LocalDate.of(2017, 2, 1))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/startdate", 123L)
                .param("projectStartDate", "2017-02-01")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}"));

        verify(projectDetailsServiceMock).updateProjectStartDate(123L, LocalDate.of(2017, 2, 1));
    }

    @Test
    public void updateStartDateButDateInPast() throws Exception {

        when(projectDetailsServiceMock.updateProjectStartDate(123L, LocalDate.of(2015, 1, 1))).thenReturn(serviceFailure(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));

        mockMvc.perform(post("/project/{id}/startdate", 123L)
                .param("projectStartDate", "2015-01-01")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isBadRequest())
                .andDo(document("project/{method-name}"));
    }

    @Test
    public void updateStartDateButDateNotFirstOfMonth() throws Exception {

        when(projectDetailsServiceMock.updateProjectStartDate(123L, LocalDate.of(2015, 1, 5))).thenReturn(serviceFailure(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));

        mockMvc.perform(post("/project/{id}/startdate", 123L)
                .param("projectStartDate", "2015-01-05")
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isBadRequest())
                .andDo(document("project/{method-name}"));
    }

    @Test
    public void updateProjectDuration() throws Exception {
        long projectId = 1L;
        long durationInMonths = 18L;

        when(projectDetailsServiceMock.updateProjectDuration(projectId, durationInMonths)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/duration/{durationInMonths}", projectId, durationInMonths)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which project duration is being updated"),
                                parameterWithName("durationInMonths").description("The new project duration to be set")
                        )
                ));
    }

    @Test
    public void setProjectManager() throws Exception {
        Long project1Id = 1L;
        Long projectManagerId = 8L;

        when(projectDetailsServiceMock.setProjectManager(project1Id, projectManagerId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/project-manager/{projectManagerId}", project1Id, projectManagerId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the project"),
                                parameterWithName("projectManagerId").description("User id of the Project Manager being assigned")
                        )
                ));
    }

    @Test
    public void setProjectManagerButInvalidProjectManager() throws Exception {
        Long project1Id = 1L;
        Long projectManagerId = 8L;

        when(projectDetailsServiceMock.setProjectManager(project1Id, projectManagerId)).thenReturn(serviceFailure(PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_LEAD_PARTNER));

        mockMvc.perform(post("/project/{id}/project-manager/{projectManagerId}", project1Id, projectManagerId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isBadRequest())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the project"),
                                parameterWithName("projectManagerId").description("User id of the Project Manager being assigned")
                        )
                ));
    }

    @Test
    public void updateFinanceContact() throws Exception {
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(123L, 456L);
        when(projectDetailsServiceMock.updateFinanceContact(composite, 789L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/finance-contact?financeContact=789", 123L, 456L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the Project that is having a Finance Contact applied to"),
                                parameterWithName("organisationId").description("Id of the Organisation that is having its Finance Contact set")
                        ),
                        requestParameters(
                                parameterWithName("financeContact").description("Id of the user who is to be the Finance Contact for the given Project and Organisation")
                        ))
                );
    }

    @Test
    public void updatePartnerProjectLocation() throws Exception {

        Long projectId = 1L;
        Long organisationId = 2L;
        PostcodeAndTownResource postcodeAndTown = new PostcodeAndTownResource("TW14 9QG", null);

        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(projectId, organisationId);
        when(projectDetailsServiceMock.updatePartnerProjectLocation(composite, postcodeAndTown)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/partner-project-location", projectId, organisationId)
                .contentType(APPLICATION_JSON)
                .content(toJson(postcodeAndTown))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the Project"),
                                parameterWithName("organisationId").description("Id of the Organisation")
                        ))
                );

        verify(projectDetailsServiceMock).updatePartnerProjectLocation(composite, postcodeAndTown);
    }

    @Test
    public void updateFinanceContactButUserIsNotOnProjectForOrganisation() throws Exception {
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(123L, 456L);

        when(projectDetailsServiceMock.updateFinanceContact(composite, 789L)).thenReturn(serviceFailure(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION));

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/finance-contact?financeContact=789", 123L, 456L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isBadRequest())
                .andDo(document("project/{method-name}"));
    }

    @Test
    public void updateFinanceContactButUserIsNotPartnerOnProjectForOrganisation() throws Exception {
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(123L, 456L);

        when(projectDetailsServiceMock.updateFinanceContact(composite, 789L)).thenReturn(serviceFailure(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION));

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/finance-contact?financeContact=789", 123L, 456L)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isBadRequest())
                .andDo(document("project/{method-name}"));
    }

    @Test
    public void inviteProjectManager() throws Exception {
        Long projectId = 123L;
        ProjectUserInviteResource invite = newProjectUserInviteResource().build();
        when(projectDetailsServiceMock.inviteProjectManager(projectId, invite)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/invite-project-manager", projectId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(toJson(invite)))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project that the project manager is being invited to")
                        )
                ));
    }

    @Test
    public void inviteFinanceContact() throws Exception {
        Long projectId = 123L;
        ProjectUserInviteResource invite = newProjectUserInviteResource().build();
        when(projectDetailsServiceMock.inviteFinanceContact(projectId, invite)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/invite-finance-contact", projectId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(toJson(invite)))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project that the finance contact is being invited to")
                        )
                ));
    }

    @Test
    public void updateProjectAddress() throws Exception {

        long projectId = 123L;
        long leadOrganisationId = 456L;

        AddressResource address = newAddressResource().build();

        when(projectDetailsServiceMock.updateProjectAddress(projectId, address)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/address", projectId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(toJson(address)))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project that the project address is being set on")
                        )
                ));
    }
}

