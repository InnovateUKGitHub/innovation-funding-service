package org.innovateuk.ifs.project.monitoringofficer.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.project.builder.LegacyMonitoringOfficerResourceBuilder;
import org.innovateuk.ifs.project.monitoringofficer.controller.LegacyMonitoringOfficerController;
import org.innovateuk.ifs.project.monitoringofficer.resource.LegacyMonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoringofficer.transactional.LegacyMonitoringOfficerService;
import org.innovateuk.ifs.project.monitoringofficer.transactional.SaveMonitoringOfficerResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.LegacyMonitoringOfficerDocs.legacyMonitoringOfficerResourceFields;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LegacyMonitoringOfficerControllerDocumentation extends BaseControllerMockMVCTest<LegacyMonitoringOfficerController> {

    private LegacyMonitoringOfficerResource monitoringOfficerResource;

    @Mock
    private LegacyMonitoringOfficerService monitoringOfficerServiceMock;

    @Before
    public void setUp() {

        monitoringOfficerResource = LegacyMonitoringOfficerResourceBuilder.newMonitoringOfficerResource()
                .withId(null)
                .withProject(1L)
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();
    }

    @Override
    protected LegacyMonitoringOfficerController supplyControllerUnderTest() {
        return new LegacyMonitoringOfficerController();
    }

    @Test
    public void saveMoWithDiffProjectIdInUrlAndMoResource() throws Exception {

        Long projectId = 1L;

        LegacyMonitoringOfficerResource monitoringOfficerResource = LegacyMonitoringOfficerResourceBuilder.newMonitoringOfficerResource()
                .withId(null)
                .withProject(3L)
                .withFirstName("abc")
                .withLastName("xyz")
                .withEmail("abc.xyz@gmail.com")
                .withPhoneNumber("078323455")
                .build();

        when(monitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).
                thenReturn(serviceFailure(new Error(PROJECT_SETUP_PROJECT_ID_IN_URL_MUST_MATCH_PROJECT_ID_IN_MONITORING_OFFICER_RESOURCE)));


        mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(monitoringOfficerResource))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isBadRequest())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Monitoring Officer is assigned")
                        ),
                        requestFields(legacyMonitoringOfficerResourceFields)
                ));

        verify(monitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);

        // Ensure that notification is not sent when there is error whilst saving
        verify(monitoringOfficerServiceMock, never()).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

    }

    @Test
    public void saveMoWhenProjectDetailsNotYetSubmitted() throws Exception {

        Long projectId = 1L;

        when(monitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).
                thenReturn(serviceFailure(new Error(PROJECT_SETUP_MONITORING_OFFICER_CANNOT_BE_ASSIGNED_UNTIL_PROJECT_DETAILS_SUBMITTED)));

        mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(monitoringOfficerResource))
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isBadRequest())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Monitoring Officer is assigned")
                        ),
                        requestFields(legacyMonitoringOfficerResourceFields)
                ));

        verify(monitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);

        // Ensure that notification is not sent when there is error whilst saving
        verify(monitoringOfficerServiceMock, never()).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

    }

    @Test
    public void saveMoWhenUnableToSendNotifications() throws Exception {

        Long projectId = 1L;

        SaveMonitoringOfficerResult successResult = new SaveMonitoringOfficerResult();
        when(monitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).thenReturn(serviceSuccess(successResult));
        when(monitoringOfficerServiceMock.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource)).
                thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE)));

        mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(monitoringOfficerResource)))
                .andExpect(status().isInternalServerError())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Monitoring Officer is assigned")
                        ),
                        requestFields(legacyMonitoringOfficerResourceFields)
                ));

        verify(monitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);
        verify(monitoringOfficerServiceMock).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

    }

    @Test
    public void saveMonitoringOfficer() throws Exception {

        Long projectId = 1L;

        SaveMonitoringOfficerResult successResult = new SaveMonitoringOfficerResult();
        when(monitoringOfficerServiceMock.saveMonitoringOfficer(projectId, monitoringOfficerResource)).thenReturn(serviceSuccess(successResult));
        when(monitoringOfficerServiceMock.notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource)).
                thenReturn(serviceSuccess());


        mockMvc.perform(put("/project/{projectId}/monitoring-officer", projectId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(monitoringOfficerResource)))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project to which the Monitoring Officer is assigned")
                        ),
                        requestFields(legacyMonitoringOfficerResourceFields)
                ));

        verify(monitoringOfficerServiceMock).saveMonitoringOfficer(projectId, monitoringOfficerResource);
        verify(monitoringOfficerServiceMock).notifyStakeholdersOfMonitoringOfficerChange(monitoringOfficerResource);

    }
}
