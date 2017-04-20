package org.innovateuk.ifs.project.monitoringofficer.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.project.builder.MonitoringOfficerResourceBuilder;
import org.innovateuk.ifs.project.monitoringofficer.controller.ProjectMonitoringOfficerController;
import org.innovateuk.ifs.project.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.transactional.SaveMonitoringOfficerResult;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.MonitoringOfficerDocs.monitoringOfficerResourceFields;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectMonitoringOfficerControllerDocumentation extends BaseControllerMockMVCTest<ProjectMonitoringOfficerController> {

    private RestDocumentationResultHandler document;

    private MonitoringOfficerResource monitoringOfficerResource;

    @Before
    public void setUp() {

        monitoringOfficerResource = MonitoringOfficerResourceBuilder.newMonitoringOfficerResource()
                .withId(null)
                .withProject(1L)
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();
    }

    @Override
    protected ProjectMonitoringOfficerController supplyControllerUnderTest() {
        return new ProjectMonitoringOfficerController();
    }

    @Before
    public void setup(){
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void saveMoWithDiffProjectIdInUrlAndMoResource() throws Exception {

        Long projectId = 1L;

        MonitoringOfficerResource monitoringOfficerResource = MonitoringOfficerResourceBuilder.newMonitoringOfficerResource()
                .withId(null)
                .withProject(3L)
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();

        when(projectMonitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).
                thenReturn(serviceFailure(new Error(PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE)));


        mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(monitoringOfficerResource)))
                .andExpect(status().isBadRequest())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Monitoring Officer is assigned")
                        ),
                        requestFields(monitoringOfficerResourceFields)
                ));

        verify(projectMonitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);

        // Ensure that notification is not sent when there is error whilst saving
        verify(projectMonitoringOfficerServiceMock, never()).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

    }

    @Test
    public void saveMoWhenProjectDetailsNotYetSubmitted() throws Exception {

        Long projectId = 1L;

        when(projectMonitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).
                thenReturn(serviceFailure(new Error(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED)));

        mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(monitoringOfficerResource)))
                .andExpect(status().isBadRequest())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Monitoring Officer is assigned")
                        ),
                        requestFields(monitoringOfficerResourceFields)
                ));

        verify(projectMonitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);

        // Ensure that notification is not sent when there is error whilst saving
        verify(projectMonitoringOfficerServiceMock, never()).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

    }

    @Test
    public void saveMoWhenUnableToSendNotifications() throws Exception {

        Long projectId = 1L;

        SaveMonitoringOfficerResult successResult = new SaveMonitoringOfficerResult();
        when(projectMonitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).thenReturn(serviceSuccess(successResult));
        when(projectMonitoringOfficerServiceMock.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource)).
                thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE)));

        mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(monitoringOfficerResource)))
                .andExpect(status().isInternalServerError())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Monitoring Officer is assigned")
                        ),
                        requestFields(monitoringOfficerResourceFields)
                ));

        verify(projectMonitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);
        verify(projectMonitoringOfficerServiceMock).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

    }

    @Test
    public void saveMonitoringOfficer() throws Exception {

        Long projectId = 1L;

        SaveMonitoringOfficerResult successResult = new SaveMonitoringOfficerResult();
        when(projectMonitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).thenReturn(serviceSuccess(successResult));
        when(projectMonitoringOfficerServiceMock.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource)).
                thenReturn(serviceSuccess());


        mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(monitoringOfficerResource)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Monitoring Officer is assigned")
                        ),
                        requestFields(monitoringOfficerResourceFields)
                ));

        verify(projectMonitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);
        verify(projectMonitoringOfficerServiceMock).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

    }
}
