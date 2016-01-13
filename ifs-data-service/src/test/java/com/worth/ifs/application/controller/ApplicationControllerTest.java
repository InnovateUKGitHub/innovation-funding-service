package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.InviteCollaboratorResource;
import com.worth.ifs.notifications.resource.ExternalUserNotificationTarget;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import com.worth.ifs.util.JsonStatusResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.notifications.builders.NotificationBuilder.newNotification;
import static com.worth.ifs.transactional.ServiceResult.success;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationControllerTest extends BaseControllerMockMVCTest<ApplicationController> {

    private final Log log = LogFactory.getLog(getClass());

    @Override
    protected ApplicationController supplyControllerUnderTest() {
        return new ApplicationController();
    }

    @Test
    public void applicationControllerShouldReturnApplicationById() throws Exception {
        ApplicationResource testApplication1 = new ApplicationResource(new Application(null, "testApplication1Name", null, null, 1L));
        ApplicationResource testApplication2 = new ApplicationResource(new Application(null, "testApplication2Name", null, null, 2L));


        when(applicationService.getApplicationById(testApplication1.getId())).thenReturn(testApplication1);
        when(applicationService.getApplicationById(testApplication2.getId())).thenReturn(testApplication2);

        mockMvc.perform(get("/application/normal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("testApplication1Name")))
                .andDo(document("application/get-application",
                        responseFields(
                                fieldWithPath("id").description("Id of the application"),
                                fieldWithPath("name").description("Name of the application"),
                                fieldWithPath("startDate").description("Estimated timescales: project start date"),
                                fieldWithPath("durationInMonths").description("Estimated timescales: project duration in months"),
                                fieldWithPath("processRoleIds").description("processRoles"),
                                fieldWithPath("applicationStatus").description("Application Status Id"),
                                fieldWithPath("competitionId").description("Competition Id"))));
        mockMvc.perform(get("/application/normal/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("testApplication2Name")));
    }

    @Test
    public void applicationControllerShouldReturnApplicationByUserId() throws Exception {
        User testUser2 = new User(2L, "testUser2", "email2@email.nl", "password", "test/image/url/2", "testToken456def", null);
        User testUser1 = new User(1L, "testUser1", "email1@email.nl", "password", "test/image/url/1", "testToken123abc", null);

        ApplicationResource testApplication1 = new ApplicationResource(new Application(null, "testApplication1Name", null, null, 1L));
        ApplicationResource testApplication2 = new ApplicationResource(new Application(null, "testApplication2Name", null, null, 2L));
        ApplicationResource testApplication3 = new ApplicationResource(new Application(null, "testApplication3Name", null, null, 3L));

        when(applicationService.findByUserId(testUser1.getId())).thenReturn(Arrays.asList(testApplication1, testApplication2));
        when(applicationService.findByUserId(testUser2.getId())).thenReturn(Arrays.asList(testApplication2, testApplication3));

        mockMvc.perform(get("/application/findByUser/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]name", is("testApplication1Name")))
                .andExpect(jsonPath("[0]id", is(1)))
                .andExpect(jsonPath("[1]name", is("testApplication2Name")))
                .andExpect(jsonPath("[1]id", is(2)))
                .andDo(document("application/find-user-applications"));
        mockMvc.perform(get("/application/findByUser/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]name", is("testApplication2Name")))
                .andExpect(jsonPath("[0]id", is(2)))
                .andExpect(jsonPath("[1]name", is("testApplication3Name")))
                .andExpect(jsonPath("[1]id", is(3)));
    }

    @Test
    public void applicationControllerShouldReturnAllApplications() throws Exception {
        int applicationNumber = 3;
        List<ApplicationResource> applications = simpleMap(newApplication().build(applicationNumber), a -> new ApplicationResource(a));
        when(applicationService.findAll()).thenReturn(applications);

        mockMvc.perform(get("/application/").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(applicationNumber)))
                .andDo(document("application/find-all-applications"));
    }

    @Test
    public void applicationControllerCanCreateApplication() throws Exception {
        Long competitionId = 1L;
        Long userId = 1L;
        String applicationName = "testApplication";
        String roleName = UserRoleType.LEADAPPLICANT.getName();

        Application application = new Application();
        application.setName(applicationName);
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode applicationNameNode = mapper.createObjectNode().put("name", application.getName());

        when(applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(competitionId, userId, applicationNameNode)).thenReturn(new ApplicationResource(application));

        mockMvc.perform(post("/application/createApplicationByName/" + competitionId + "/" + userId, "json")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(applicationNameNode)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", notNullValue()))
                .andDo(document("application/create-application"));
    }

    @Test
    public void testInviteCollaboratorToApplication() throws Exception {

        InviteCollaboratorResource invite = new InviteCollaboratorResource("The Recipient", "recipient@example.com");
        String inviteBody = new ObjectMapper().writeValueAsString(invite);

        Notification successfullySentNotification = newNotification().withTargets(singletonList(new ExternalUserNotificationTarget("The Recipient", "recipient@example.com"))).build();

        when(applicationService.inviteCollaboratorToApplication(123L, invite)).thenReturn(success(successfullySentNotification));

        MvcResult response = mockMvc.
                perform(
                        post("/application/123/invitecollaborator").
                                header("Content-Type", "application/json").
                                header("IFS_AUTH_TOKEN", "123abc").
                                content(inviteBody)
                ).
                andExpect(status().isAccepted()).
                andDo(document("application/invite-collaborator",
                        requestHeaders(
                                headerWithName("Content-Type").description("Needs to be application/json"),
                                headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                        ),
                        responseFields(
                                fieldWithPath("message").description("A plain text descriptive message of the action that was performed e.g. \"Notification sent successfully\"")
                        ))
                ).
                andReturn();

        String content = response.getResponse().getContentAsString();
        JsonStatusResponse jsonResponse = new ObjectMapper().readValue(content, JsonStatusResponse.class);
        assertEquals("Notification sent successfully", jsonResponse.getMessage());
    }
}