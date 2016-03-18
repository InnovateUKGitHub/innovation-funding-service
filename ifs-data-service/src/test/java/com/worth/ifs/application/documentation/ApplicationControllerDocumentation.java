package com.worth.ifs.application.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.controller.ApplicationController;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.application.transactional.ApplicationServiceImpl.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.documentation.ApplicationDocs.applicationResourceBuilder;
import static com.worth.ifs.documentation.ApplicationDocs.applicationResourceFields;
import static com.worth.ifs.user.domain.UserRoleType.LEADAPPLICANT;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

public class ApplicationControllerDocumentation extends BaseControllerMockMVCTest<ApplicationController> {

    @Override
    protected ApplicationController supplyControllerUnderTest() {
        return new ApplicationController();
    }

    @Test
    public void documentGetApplicationById() throws Exception {
        Long application1Id = 1L;
        ApplicationResource testApplicationResource1 = applicationResourceBuilder.build();

        when(applicationService.getApplicationById(application1Id)).thenReturn(serviceSuccess(testApplicationResource1));

        mockMvc.perform(get("/application/{id}", application1Id))
                .andDo(document("application/get-application",
                        pathParameters(
                                parameterWithName("id").description("Id of the application that is being requested")
                        ),
                        responseFields(applicationResourceFields)
                ));
    }

    @Test
    public void documentFindAll() throws Exception {
        int applicationNumber = 3;
        List<ApplicationResource> applications = applicationResourceBuilder.build(applicationNumber);
        when(applicationService.findAll()).thenReturn(serviceSuccess(applications));

        mockMvc.perform(get("/application/").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(document("application/find-all-applications",
                        responseFields(
                                fieldWithPath("[]").description("List of applications the user is allowed to see")
                        )
                ));
    }

    @Test
    public void documentFindByUserId() throws Exception {
        Long userId = 1L;
        User testUser1 = new User(userId, "test", "User1", "email1@email.nl", "testToken123abc", null, "my-uid2");

        List<ApplicationResource> applications = applicationResourceBuilder.build(2);

        when(applicationService.findByUserId(testUser1.getId())).thenReturn(serviceSuccess(applications));

        mockMvc.perform(get("/application/findByUser/{id}", userId))
                .andDo(document("application/find-user-applications",
                        pathParameters(
                                parameterWithName("id").description("Id of the user the applications are being requested for")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of applications linked to the user id used in the request. Only contains applications the requesting user can see")
                        )));
    }

    @Test
    public void documentSaveApplicationDetails() throws Exception{
        Long applicationId = 1L;
        ObjectMapper mapper = new ObjectMapper();

        ApplicationResource testApplicationResource1 = applicationResourceBuilder.build();

        when(applicationService.saveApplicationDetails(applicationId, testApplicationResource1)).thenReturn(serviceSuccess(null));

        mockMvc.perform(post("/application/saveApplicationDetails/{id}", applicationId)
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(testApplicationResource1))
                )
                .andDo(document("application/save-application-details",
                    pathParameters(
                            parameterWithName("id").description("Id of the application that needs to be saved")
                    ),
                    requestFields(applicationResourceFields)
                ));
    }

    @Test
    public void documentGetProgressPercentageByApplicationId() throws Exception{
        Long applicationId = 1L;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("completedPercentage", 10);

        when(applicationService.getProgressPercentageNodeByApplicationId(applicationId)).thenReturn(serviceSuccess(node));

        mockMvc.perform(get("/application/getProgressPercentageByApplicationId/{applicationId}", applicationId))
                .andDo(document("application/get-progress-percentage",
                    pathParameters(
                        parameterWithName("applicationId").description("Id of the application of which the percentage is requested")
                    ),
                    responseFields(
                            fieldWithPath("completedPercentage").description("application completion percentage")
                    )
                ));
    }

    @Test
    public void documentUpdateApplicationStatus() throws Exception {
        Long applicationId = 1L;
        Long statusId = 1L;

        ApplicationResource applicationResource = applicationResourceBuilder.build();

        when(applicationService.updateApplicationStatus(applicationId, statusId)).thenReturn(serviceSuccess(applicationResource));

        mockMvc.perform(put("/application/updateApplicationStatus?applicationId={applicationId}&statusId={statusId}", applicationId, statusId))
                .andDo(document("application/update-application-status",
                    requestParameters(
                        parameterWithName("applicationId").description("id of the application for which to update the application status"),
                        parameterWithName("statusId").description("new status id")
                    )
                ));
    }

    @Test
    public void documentApplicationReadyForSubmit() throws Exception{
        Long applicationId = 1L;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put(READY_FOR_SUBMIT, true);
        node.put(PROGRESS, 10);
        node.put(RESEARCH_PARTICIPATION, 20.5);
        node.put(RESEARCH_PARTICIPATION_VALID, true);
        node.put(ALL_SECTION_COMPLETE, true);

        when(applicationService.applicationReadyForSubmit(applicationId)).thenReturn(serviceSuccess(node));

        mockMvc.perform(get("/application/applicationReadyForSubmit/{applicationId}", applicationId))
                .andDo(document("application/ready-for-submit",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application")
                        ),
                        responseFields(
                                fieldWithPath(READY_FOR_SUBMIT).description("application is ready to be submitted"),
                                fieldWithPath(PROGRESS).description("Progress percentage of the application"),
                                fieldWithPath(RESEARCH_PARTICIPATION).description("Research participation percentage"),
                                fieldWithPath(RESEARCH_PARTICIPATION_VALID).description("Research participation percentage is valid"),
                                fieldWithPath(ALL_SECTION_COMPLETE).description("all sections have been completed")
                        )
                ));
    }

    @Test
    public void documentGetApplicationsByCompetitionIdAndUserId() throws Exception{
        Long competitionId = 1L;
        Long userId = 1L;
        UserRoleType role = LEADAPPLICANT;

        List<ApplicationResource> applicationResources = applicationResourceBuilder.build(2);

        when(applicationService.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role)).thenReturn(serviceSuccess(applicationResources));

        mockMvc.perform(get("/application/getApplicationsByCompetitionIdAndUserId/{competitionId}/{userId}/{role}", competitionId, userId, role))
                .andDo(document("application/get-progress-percentage",
                        pathParameters(
                                parameterWithName("competitionId").description("Competition Id"),
                                parameterWithName("userId").description("User Id"),
                                parameterWithName("role").description("UserRoleType")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of applications")
                        )
                ));
    }

    @Test
    public void documentCreateApplicationByApplicationNameForUserIdAndCompetitionId() throws Exception {
        Long competitionId = 1L;
        Long userId = 1L;
        String applicationName = "testApplication";

        ApplicationResource applicationResource = applicationResourceBuilder.build();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode applicationNameNode = mapper.createObjectNode().put("name", applicationName);

        when(applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(competitionId, userId, applicationName)).thenReturn(serviceSuccess(applicationResource));

        mockMvc.perform(post("/application/createApplicationByName/{competitionId}/{userId}", competitionId, userId, "json")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(applicationNameNode)))
                .andDo(document("application/create-application",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition the new application is being created for."),
                                parameterWithName("userId").description("Id of the user the new application is being created for.")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of the application that will be created")
                        ),
                        responseFields(applicationResourceFields)
                ));
    }
}
