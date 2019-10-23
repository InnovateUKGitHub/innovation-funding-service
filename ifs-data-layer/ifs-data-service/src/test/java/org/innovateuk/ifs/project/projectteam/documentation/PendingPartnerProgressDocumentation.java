package org.innovateuk.ifs.project.projectteam.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.projectteam.controller.PendingPartnerProgressController;
import org.innovateuk.ifs.project.projectteam.transactional.PendingPartnerProgressService;
import org.innovateuk.ifs.project.resource.PendingPartnerProgressResource;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId.id;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class PendingPartnerProgressDocumentation extends BaseControllerMockMVCTest<PendingPartnerProgressController> {

    private static final long projectId = 123L;
    private static final long organisationId = 456L;
    private static final String baseUrl = "/project/{projectId}/organisation/{organisationId}/pending-partner-progress";

    @Mock
    private PendingPartnerProgressService pendingPartnerProgressService;

    @Override
    protected PendingPartnerProgressController supplyControllerUnderTest() {
        return new PendingPartnerProgressController();
    }

    @Test
    public void getPendingPartnerProgress() throws Exception {
        PendingPartnerProgressResource resource = new PendingPartnerProgressResource();

        when(pendingPartnerProgressService.getPendingPartnerProgress(id(projectId, organisationId))).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get(baseUrl, projectId, organisationId))
                .andExpect(status().isOk())
                .andDo(document("pending-partner-progress/{method-name}",
                                pathParameters(
                                        parameterWithName("projectId").description("Id of project to get the partner progress of"),
                                        parameterWithName("organisationId").description("Id of the organisation to get the partner progress of"))));

        verify(pendingPartnerProgressService).getPendingPartnerProgress(id(projectId, organisationId));
    }

    @Test
    public void markYourOrganisationComplete() throws Exception {
        when(pendingPartnerProgressService.markYourOrganisationComplete(id(projectId, organisationId))).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseUrl + "/your-organisation-complete", projectId, organisationId))
                .andExpect(status().isOk())
                .andDo(document("pending-partner-progress/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project to change the status of"),
                                parameterWithName("organisationId").description("Id of the organisation to change the status of"))));

        verify(pendingPartnerProgressService).markYourOrganisationComplete(id(projectId, organisationId));
    }

    @Test
    public void markYourFundingComplete() throws Exception {
        when(pendingPartnerProgressService.markYourFundingComplete(id(projectId, organisationId))).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseUrl + "/your-funding-complete", projectId, organisationId))
                .andExpect(status().isOk())
                .andDo(document("pending-partner-progress/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project to change the status of"),
                                parameterWithName("organisationId").description("Id of the organisation to change the status of"))));

        verify(pendingPartnerProgressService).markYourFundingComplete(id(projectId, organisationId));
    }

    @Test
    public void markTermsAndConditionsComplete() throws Exception {
        when(pendingPartnerProgressService.markTermsAndConditionsComplete(id(projectId, organisationId))).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseUrl + "/terms-and-conditions-complete", projectId, organisationId))
                .andExpect(status().isOk())
                .andDo(document("pending-partner-progress/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project to change the status of"),
                                parameterWithName("organisationId").description("Id of the organisation to change the status of"))));

        verify(pendingPartnerProgressService).markTermsAndConditionsComplete(id(projectId, organisationId));
    }

    @Test
    public void markYourOrganisationIncomplete() throws Exception {
        when(pendingPartnerProgressService.markYourOrganisationIncomplete(id(projectId, organisationId))).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseUrl + "/your-organisation-incomplete", projectId, organisationId))
                .andExpect(status().isOk())
                .andDo(document("pending-partner-progress/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project to change the status of"),
                                parameterWithName("organisationId").description("Id of the organisation to change the status of"))));

        verify(pendingPartnerProgressService).markYourOrganisationIncomplete(id(projectId, organisationId));
    }

    @Test
    public void markYourFundingIncomplete() throws Exception {
        when(pendingPartnerProgressService.markYourFundingIncomplete(id(projectId, organisationId))).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseUrl + "/your-funding-incomplete", projectId, organisationId))
                .andExpect(status().isOk())
                .andDo(document("pending-partner-progress/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project to change the status of"),
                                parameterWithName("organisationId").description("Id of the organisation to change the status of"))));

        verify(pendingPartnerProgressService).markYourFundingIncomplete(id(projectId, organisationId));
    }

    @Test
    public void markTermsAndConditionsIncomplete() throws Exception {
        when(pendingPartnerProgressService.markTermsAndConditionsIncomplete(id(projectId, organisationId))).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseUrl + "/terms-and-conditions-incomplete", projectId, organisationId))
                .andExpect(status().isOk())
                .andDo(document("pending-partner-progress/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project to change the status of"),
                                parameterWithName("organisationId").description("Id of the organisation to change the status of"))));

        verify(pendingPartnerProgressService).markTermsAndConditionsIncomplete(id(projectId, organisationId));
    }

    @Test
    public void completePartnerSetup() throws Exception {
        when(pendingPartnerProgressService.completePartnerSetup(id(projectId, organisationId))).thenReturn(serviceSuccess());

        mockMvc.perform(post(baseUrl, projectId, organisationId))
                .andExpect(status().isOk())
                .andDo(document("pending-partner-progress/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of project to change the status of"),
                                parameterWithName("organisationId").description("Id of the organisation to change the status of"))));

        verify(pendingPartnerProgressService).completePartnerSetup(id(projectId, organisationId));
    }
}
