package org.innovateuk.ifs.application.documentation;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.builder.ApplicationResourceBuilder;
import org.innovateuk.ifs.application.controller.ApplicationController;
import org.innovateuk.ifs.application.resource.*;
import org.innovateuk.ifs.application.transactional.ApplicationDeletionService;
import org.innovateuk.ifs.application.transactional.ApplicationNotificationService;
import org.innovateuk.ifs.application.transactional.ApplicationProgressService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.error.ValidationMessages;
import org.innovateuk.ifs.crm.transactional.CrmService;
import org.innovateuk.ifs.documentation.ApplicationDocs;
import org.innovateuk.ifs.documentation.InnovationAreaResourceDocs;
import org.innovateuk.ifs.documentation.PageResourceDocs;
import org.innovateuk.ifs.documentation.ResearchCategoryResourceDocs;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.util.JsonMappingUtil;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ApplicationDocs.applicationResourceBuilder;
import static org.innovateuk.ifs.documentation.ApplicationDocs.applicationResourceFields;
import static org.innovateuk.ifs.documentation.ApplicationIneligibleSendResourceDocs.applicationIneligibleSendResourceBuilder;
import static org.innovateuk.ifs.documentation.ApplicationIneligibleSendResourceDocs.applicationIneligibleSendResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationControllerDocumentation extends BaseControllerMockMVCTest<ApplicationController> {

    @Mock
    private ApplicationService applicationServiceMock;

    @Mock
    private ApplicationNotificationService applicationNotificationServiceMock;

    @Mock
    private ApplicationProgressService applicationProgressServiceMock;

    @Mock
    private CrmService crmService;

    @Mock
    private ApplicationDeletionService applicationDeletionService;

    @Override
    protected ApplicationController supplyControllerUnderTest() {
        return new ApplicationController();
    }


    @Test
    public void getApplicationById() throws Exception {
        Long application1Id = 1L;
        ApplicationResource testApplicationResource1 = applicationResourceBuilder.build();

        when(applicationServiceMock.getApplicationById(application1Id)).thenReturn(serviceSuccess(testApplicationResource1));

        mockMvc.perform(get("/application/{id}", application1Id)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the application that is being requested")
                        ),
                        responseFields(applicationResourceFields)
                        .andWithPrefix("researchCategory.", ResearchCategoryResourceDocs.researchCategoryResourceFields)
                        .andWithPrefix("innovationArea.", InnovationAreaResourceDocs.innovationAreaResourceFields)
                ));
    }

    @Test
    public void findAll() throws Exception {
        int applicationNumber = 3;
        List<ApplicationResource> applications = applicationResourceBuilder.build(applicationNumber);
        when(applicationServiceMock.findAll()).thenReturn(serviceSuccess(applications));

        mockMvc.perform(get("/application/").contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(
                        document("application/{method-name}",
                                responseFields(
                                        fieldWithPath("[]").description("List of applications the user is allowed to see")
                                ).andWithPrefix("[].", applicationResourceFields)
                                .andWithPrefix("[].researchCategory.", ResearchCategoryResourceDocs.researchCategoryResourceFields)
                                .andWithPrefix("[].innovationArea.", InnovationAreaResourceDocs.innovationAreaResourceFields)));
    }

    @Test
    public void findByUserId() throws Exception {
        Long userId = 1L;
        User testUser1 = new User(userId, "test", "User1", "email1@email.nl", "testToken123abc", "my-uid2");

        List<ApplicationResource> applications = applicationResourceBuilder.build(2);

        when(applicationServiceMock.findByUserId(testUser1.getId())).thenReturn(serviceSuccess(applications));

        mockMvc.perform(get("/application/find-by-user/{id}", userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the user the applications are being requested for")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of applications linked to the user id used in the request. Only contains applications the requesting user can see")
                        ).andWithPrefix("[].", applicationResourceFields)
                         .andWithPrefix("[].researchCategory.", ResearchCategoryResourceDocs.researchCategoryResourceFields)
                         .andWithPrefix("[].innovationArea.", InnovationAreaResourceDocs.innovationAreaResourceFields)));
    }

    @Test
    public void wildcardSearchById() throws Exception {
        String searchString = "12";
        int pageNumber = 1;
        int pageSize = 20;

        List<ApplicationResource> applicationResources = ApplicationResourceBuilder.newApplicationResource().build(4);
        ApplicationPageResource applicationPageResource = new ApplicationPageResource(applicationResources.size(), 5, applicationResources, pageNumber, pageSize);

        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        when(applicationServiceMock.wildcardSearchById(searchString, pageRequest)).thenReturn(serviceSuccess(applicationPageResource));

        mockMvc.perform(get("/application/wildcard-search-by-id?searchString=" + searchString + "&page=" + pageNumber + "&size=" + pageSize)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andExpect(content().json(JsonMappingUtil.toJson(applicationPageResource)))
                .andDo(document("application/{method-name}",
                                requestParameters(
                                        parameterWithName("searchString").description("The application id on which wildcard search will be performed"),
                                        parameterWithName("page").description("The page number to be retrieved"),
                                        parameterWithName("size").description("The page size")
                                ),
                                responseFields(PageResourceDocs.pageResourceFields)
                                        .andWithPrefix("content[].", ApplicationDocs.applicationResourceFields)

                ));
    }

    @Test
    public void saveApplicationDetails() throws Exception {
        Long applicationId = 1L;

        ValidationMessages validationMessages = new ValidationMessages();
        ApplicationResource testApplicationResource1 = applicationResourceBuilder.build();

        when(applicationServiceMock.saveApplicationDetails(applicationId, testApplicationResource1)).thenReturn(serviceSuccess(validationMessages));

        mockMvc.perform(post("/application/save-application-details/{id}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testApplicationResource1)))
                .andExpect(status().isOk())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the application that needs to be saved")
                        )
                ));
    }

    @Test
    public void updateApplicationStatus() throws Exception {
        Long applicationId = 1L;
        ApplicationState state = ApplicationState.APPROVED;

        ApplicationResource applicationResource = applicationResourceBuilder.build();

        when(applicationServiceMock.updateApplicationState(applicationId, state)).thenReturn(serviceSuccess(applicationResource));

        mockMvc.perform(put("/application/update-application-state?applicationId={applicationId}&state={state}", applicationId, state)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("application/{method-name}",
                        requestParameters(
                                parameterWithName("applicationId").description("id of the application for which to update the application state"),
                                parameterWithName("state").description("new state id")
                        )
                ));
    }

    @Test
    public void applicationReadyForSubmit() throws Exception {
        Long applicationId = 1L;

        when(applicationProgressServiceMock.applicationReadyForSubmit(applicationId)).thenReturn(true);

        mockMvc.perform(get("/application/application-ready-for-submit/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application")
                        )
                ));
    }

    @Test
    public void getApplicationsByCompetitionIdAndUserId() throws Exception {
        Long competitionId = 1L;
        Long userId = 1L;
        List<ApplicationResource> applicationResources = applicationResourceBuilder.build(2);
        Role role= Role.LEADAPPLICANT;

        when(applicationServiceMock.getApplicationsByCompetitionIdAndUserId(competitionId, userId, role)).thenReturn(serviceSuccess(applicationResources));

        mockMvc.perform(get("/application/get-applications-by-competition-id-and-user-id/{competitionId}/{userId}/{role}", competitionId, userId, role)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Competition Id"),
                                parameterWithName("userId").description("User Id"),
                                parameterWithName("role").description("UserRoleType")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of applications")
                        ).andWithPrefix("[].", applicationResourceFields)
                                .andWithPrefix("[].researchCategory.", ResearchCategoryResourceDocs.researchCategoryResourceFields)
                                .andWithPrefix("[].innovationArea.", InnovationAreaResourceDocs.innovationAreaResourceFields)
                ));
    }

    @Test
    public void createApplicationByApplicationNameForUserIdAndCompetitionId() throws Exception {
        long competitionId = 1L;
        long userId = 1L;
        long organisationId = 1L;
        String applicationName = "testApplication";

        ApplicationResource applicationResource = applicationResourceBuilder.build();

        ObjectNode applicationNameNode = objectMapper.createObjectNode().put("name", applicationName);

        when(applicationServiceMock.createApplicationByApplicationNameForUserIdAndCompetitionId(applicationName, competitionId, organisationId, userId)).thenReturn(serviceSuccess(applicationResource));

        mockMvc.perform(post("/application/create-application-by-name/{competitionId}/{userId}/{organisationId}", competitionId, userId, organisationId, "json")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationNameNode)))
                .andExpect(status().isCreated())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition the new application is being created for."),
                                parameterWithName("userId").description("Id of the user the new application is being created for."),
                                parameterWithName("organisationId").description("Id of the organisation the new application is being created for.")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of the application that will be created")
                        ),
                        responseFields(applicationResourceFields)
                        .andWithPrefix("researchCategory.", ResearchCategoryResourceDocs.researchCategoryResourceFields)
                        .andWithPrefix("innovationArea.", InnovationAreaResourceDocs.innovationAreaResourceFields)
                ));
    }

    @Test
    public void informIneligible() throws Exception {
        long applicationId = 1L;
        ApplicationIneligibleSendResource applicationIneligibleSendResource = applicationIneligibleSendResourceBuilder.build();

        when(applicationNotificationServiceMock.informIneligible(applicationId, applicationIneligibleSendResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/application/inform-ineligible/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationIneligibleSendResource)))
                .andExpect(status().isOk())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to inform of being ineligible")
                        ),
                        requestFields(applicationIneligibleSendResourceFields)
                ));
    }

    @Test
    public void showApplicationTeam() throws Exception {
        Long applicationId = 1L;
        Long userId = 2L;

        when(applicationServiceMock.showApplicationTeam(applicationId, userId)).thenReturn(serviceSuccess(Boolean.TRUE));

        mockMvc.perform(get("/application/show-application-team/{applicationId}/{userId}", applicationId, userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application"),
                                parameterWithName("userId").description("Id of the user who wants to view the application team")
                        )
                ));
    }

    @Test
    public void getLatestEmailFundingDate() throws Exception {
        Long competitionId = 1L;

        when(applicationServiceMock.findLatestEmailFundingDateByCompetitionId(competitionId)).thenReturn(serviceSuccess(ZonedDateTime.now()));

        mockMvc.perform(get("/application/get-latest-email-funding-date/{competitionId}", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("competitionId").description("Id of the competition we want the latest email funding date from")
                        )
                ));
    }
    @Test
    public void deleteApplication() throws Exception {
        long applicationId = 1L;
        when(applicationDeletionService.deleteApplication(applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/application/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNoContent())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to delete")
                        )
                ));
    }

    @Test
    public void hideApplicationFromDashboard() throws Exception {
        long applicationId = 1L;
        long userId = 1L;
        when(applicationDeletionService.hideApplicationFromDashboard(ApplicationUserCompositeId.id(applicationId, userId))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/application/{applicationId}/hide-for-user/{userId}", applicationId, userId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isNoContent())
                .andDo(document("application/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to hide"),
                                parameterWithName("applicationId").description("Id of the users dashboard to hide from")
                        )
                ));
    }
}
