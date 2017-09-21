package org.innovateuk.ifs.project.projectdetails.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.InviteProjectResource;
import org.innovateuk.ifs.project.projectdetails.controller.ProjectDetailsController;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.time.LocalDate;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_MANAGER_MUST_BE_LEAD_PARTNER;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ProjectInviteResourceBuilder.newInviteProjectResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectDetailsControllerDocumentation extends BaseControllerMockMVCTest<ProjectDetailsController> {

    private RestDocumentationResultHandler document;

    @Override
    protected ProjectDetailsController supplyControllerUnderTest() {
        return new ProjectDetailsController();
    }

    @Before
    public void setup(){
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void updateStartDate() throws Exception {

        when(projectDetailsServiceMock.updateProjectStartDate(123L, LocalDate.of(2017, 2, 1))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/startdate", 123L).
                param("projectStartDate", "2017-02-01"))
                .andExpect(status().isOk())
                .andDo(this.document);

        verify(projectDetailsServiceMock).updateProjectStartDate(123L, LocalDate.of(2017, 2, 1));
    }

    @Test
    public void updateStartDateButDateInPast() throws Exception {

        when(projectDetailsServiceMock.updateProjectStartDate(123L, LocalDate.of(2015, 1, 1))).thenReturn(serviceFailure(PROJECT_SETUP_DATE_MUST_BE_IN_THE_FUTURE));

        mockMvc.perform(post("/project/{id}/startdate", 123L).
                param("projectStartDate", "2015-01-01"))
                .andExpect(status().isBadRequest())
                .andDo(this.document);
    }

    @Test
    public void updateStartDateButDateNotFirstOfMonth() throws Exception {

        when(projectDetailsServiceMock.updateProjectStartDate(123L, LocalDate.of(2015, 1, 5))).thenReturn(serviceFailure(PROJECT_SETUP_DATE_MUST_START_ON_FIRST_DAY_OF_MONTH));

        mockMvc.perform(post("/project/{id}/startdate", 123L).
                param("projectStartDate", "2015-01-05"))
                .andExpect(status().isBadRequest())
                .andDo(this.document);
    }

    @Test
    public void setProjectManager() throws Exception {
        Long project1Id = 1L;
        Long projectManagerId = 8L;

        when(projectDetailsServiceMock.setProjectManager(project1Id, projectManagerId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/project-manager/{projectManagerId}", project1Id, projectManagerId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
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

        mockMvc.perform(post("/project/{id}/project-manager/{projectManagerId}", project1Id, projectManagerId))
                .andExpect(status().isBadRequest())
                .andDo(this.document.snippets(
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

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/finance-contact?financeContact=789", 123L, 456L))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
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
    public void updateFinanceContactButUserIsNotOnProjectForOrganisation() throws Exception {
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(123L, 456L);

        when(projectDetailsServiceMock.updateFinanceContact(composite, 789L)).thenReturn(serviceFailure(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_USER_ON_THE_PROJECT_FOR_THE_ORGANISATION));

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/finance-contact?financeContact=789", 123L, 456L))
                .andExpect(status().isBadRequest())
                .andDo(this.document);
    }

    @Test
    public void updateFinanceContactButUserIsNotPartnerOnProjectForOrganisation() throws Exception {
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(123L, 456L);

        when(projectDetailsServiceMock.updateFinanceContact(composite, 789L)).thenReturn(serviceFailure(PROJECT_SETUP_FINANCE_CONTACT_MUST_BE_A_PARTNER_ON_THE_PROJECT_FOR_THE_ORGANISATION));

        mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/finance-contact?financeContact=789", 123L, 456L))
                .andExpect(status().isBadRequest())
                .andDo(this.document);
    }

    @Test
    public void inviteProjectManager() throws Exception {
        Long projectId = 123L;
        InviteProjectResource invite = newInviteProjectResource().build();
        when(projectDetailsServiceMock.inviteProjectManager(projectId, invite)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/invite-project-manager", projectId)
                .contentType(APPLICATION_JSON)
                .content(toJson(invite)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of project that bank details status summary is requested for")
                        )
                ));
    }

    @Test
    public void inviteFinanceContact() throws Exception {
        Long projectId = 123L;
        InviteProjectResource invite = newInviteProjectResource().build();
        when(projectDetailsServiceMock.inviteFinanceContact(projectId, invite)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/invite-finance-contact", projectId)
                .contentType(APPLICATION_JSON)
                .content(toJson(invite)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of project that bank details status summary is requested for")
                        )
                ));
    }
}

