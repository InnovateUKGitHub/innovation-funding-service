package org.innovateuk.ifs.application.documentation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.ApplicationController;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.resource.CompletedPercentageResource;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.application.transactional.ApplicationServiceImpl.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ApplicationDocs.applicationResourceBuilder;
import static org.innovateuk.ifs.documentation.ApplicationDocs.applicationResourceFields;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
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
    public void getApplicationById() throws Exception {
        Long application1Id = 1L;
        ApplicationResource testApplicationResource1 = applicationResourceBuilder.build();

        when(applicationServiceMock.getApplicationById(application1Id)).thenReturn(serviceSuccess(testApplicationResource1));

        mockMvc.perform(get("/application/{id}", application1Id))
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the application that is being requested")
                        ),
                        responseFields(applicationResourceFields)
                ));
    }

    @Test
    public void findAll() throws Exception {
        int applicationNumber = 3;
        List<ApplicationResource> applications = applicationResourceBuilder.build(applicationNumber);
        when(applicationServiceMock.findAll()).thenReturn(serviceSuccess(applications));

        mockMvc.perform(get("/application/").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(
                        document("application/{method-name}",
                        responseFields(
                                fieldWithPath("[]").description("List of applications the user is allowed to see")
                        )
                ));
    }

    @Test
    public void findByUserId() throws Exception {
        Long userId = 1L;
        User testUser1 = new User(userId, "test", "User1", "email1@email.nl", "testToken123abc", "my-uid2");

        List<ApplicationResource> applications = applicationResourceBuilder.build(2);

        when(applicationServiceMock.findByUserId(testUser1.getId())).thenReturn(serviceSuccess(applications));

        mockMvc.perform(get("/application/findByUser/{id}", userId))
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the user the applications are being requested for")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of applications linked to the user id used in the request. Only contains applications the requesting user can see")
                        )));
    }

    @Test
    public void saveApplicationDetails() throws Exception{
        Long applicationId = 1L;

        ApplicationResource testApplicationResource1 = applicationResourceBuilder.build();

        when(applicationServiceMock.saveApplicationDetails(applicationId, testApplicationResource1)).thenReturn(serviceSuccess(testApplicationResource1));

        mockMvc.perform(post("/application/saveApplicationDetails/{id}", applicationId)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testApplicationResource1))
                )
                .andDo(document("application/{method-name}",
                    pathParameters(
                            parameterWithName("id").description("Id of the application that needs to be saved")
                    )
                ));
    }

    @Test
    public void getProgressPercentageByApplicationId() throws Exception{
        Long applicationId = 1L;

        CompletedPercentageResource resource = new CompletedPercentageResource();
        resource.setCompletedPercentage(new BigDecimal("10"));

        when(applicationServiceMock.getProgressPercentageByApplicationId(applicationId)).thenReturn(serviceSuccess(resource));

        mockMvc.perform(get("/application/getProgressPercentageByApplicationId/{applicationId}", applicationId))
                .andDo(document("application/{method-name}",
                    pathParameters(
                        parameterWithName("applicationId").description("Id of the application of which the percentage is requested")
                    ),
                    responseFields(
                            fieldWithPath("completedPercentage").description("application completion percentage")
                    )
                ));
    }

    @Test
    public void updateApplicationStatus() throws Exception {
        Long applicationId = 1L;
        ApplicationState state = ApplicationState.APPROVED;

        ApplicationResource applicationResource = applicationResourceBuilder.build();

        when(applicationServiceMock.updateApplicationState(applicationId, state)).thenReturn(serviceSuccess(applicationResource));

        mockMvc.perform(put("/application/updateApplicationStatus?applicationId={applicationId}&state={state}", applicationId, state))
                .andDo(document("application/{method-name}",
                    requestParameters(
                        parameterWithName("applicationId").description("id of the application for which to update the application state"),
                        parameterWithName("state").description("new state id")
                    )
                ));
    }

    @Test
    public void applicationReadyForSubmit() throws Exception{
        Long applicationId = 1L;

        ObjectNode node = objectMapper.createObjectNode();
        node.put(READY_FOR_SUBMIT, true);
        node.put(PROGRESS, 10);
        node.put(RESEARCH_PARTICIPATION, 20.5);
        node.put(RESEARCH_PARTICIPATION_VALID, true);
        node.put(ALL_SECTION_COMPLETE, true);

        when(applicationServiceMock.applicationReadyForSubmit(applicationId)).thenReturn(serviceSuccess(node));

        mockMvc.perform(get("/application/applicationReadyForSubmit/{applicationId}", applicationId))
                .andDo(document("application/{method-name}",
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
    public void getApplicationsByCompetitionIdAndUserId() throws Exception{
        Long competitionId = 1L;
        Long userId = 1L;
        UserRoleType role = LEADAPPLICANT;

        List<ApplicationResource> applicationResources = applicationResourceBuilder.build(2);

        when(applicationServiceMock.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role)).thenReturn(serviceSuccess(applicationResources));

        mockMvc.perform(get("/application/getApplicationsByCompetitionIdAndUserId/{competitionId}/{userId}/{role}", competitionId, userId, role))
                .andDo(document("application/{method-name}",
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
    public void createApplicationByApplicationNameForUserIdAndCompetitionId() throws Exception {
        Long competitionId = 1L;
        Long userId = 1L;
        String applicationName = "testApplication";

        ApplicationResource applicationResource = applicationResourceBuilder.build();

        ObjectNode applicationNameNode = objectMapper.createObjectNode().put("name", applicationName);

        when(applicationServiceMock.createApplicationByApplicationNameForUserIdAndCompetitionId(applicationName, competitionId, userId)).thenReturn(serviceSuccess(applicationResource));

        mockMvc.perform(post("/application/createApplicationByName/{competitionId}/{userId}", competitionId, userId, "json")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationNameNode)))
                .andDo(document("application/{method-name}",
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
