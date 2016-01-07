package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.mapper.ApplicationMapper;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserRoleType;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationControllerTest extends BaseControllerMockMVCTest<ApplicationController> {

    @Mock
    protected ApplicationMapper applicationMapper;

    @Override
    protected ApplicationController supplyControllerUnderTest() {
        return new ApplicationController();
    }

    @Test
    public void applicationControllerShouldReturnApplicationById() throws Exception {
        Application testApplication1 = newApplication().withId(1L).withName("testApplication1Name").build();
        Application testApplication2 = newApplication().withId(2L).withName("testApplication2Name").build();
        ApplicationResource testApplicationResource1 = newApplicationResource().withId(1L).withName("testApplication1Name").build();
        ApplicationResource testApplicationResource2 = newApplicationResource().withId(2L).withName("testApplication2Name").build();

        when(applicationService.getApplicationById(testApplication1.getId())).thenReturn(testApplication1);
        when(applicationService.getApplicationById(testApplication2.getId())).thenReturn(testApplication2);
        when(applicationMapper.mapApplicationToResource(testApplication1)).thenReturn(testApplicationResource1);
        when(applicationMapper.mapApplicationToResource(testApplication2)).thenReturn(testApplicationResource2);

        mockMvc.perform(get("/application/normal/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(testApplicationResource1)))
                .andDo(document("application/get-application",
                        responseFields(
                                fieldWithPath("id").description("Id of the application"),
                                fieldWithPath("name").description("Name of the application"),
                                fieldWithPath("startDate").description("Estimated timescales: project start date"),
                                fieldWithPath("durationInMonths").description("Estimated timescales: project duration in months"),
                                fieldWithPath("processRoles").description("list of ProcessRole Id's"),
                                fieldWithPath("applicationStatus").description("ApplicationStatus Id"),
                                fieldWithPath("competition").description("Competition Id"),
                                fieldWithPath("applicationFinances").description("list of ApplicationFinance Id's"))));
        mockMvc.perform(get("/application/normal/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(testApplicationResource2)));
    }

    @Test
    public void applicationControllerShouldReturnApplicationByUserId() throws Exception {
        User testUser2 = new User(2L, "testUser2", "email2@email.nl", "password", "test/image/url/2", "testToken456def", null);
        User testUser1 = new User(1L, "testUser1", "email1@email.nl", "password", "test/image/url/1", "testToken123abc", null);

        Application testApplication1 = newApplication().withId(1L).withName("testApplication1Name").build();
        Application testApplication2 = newApplication().withId(2L).withName("testApplication2Name").build();
        Application testApplication3 = newApplication().withId(3L).withName("testApplication3Name").build();
        ApplicationResource testApplicationResource1 = newApplicationResource().withId(1L).withName("testApplication1Name").build();
        ApplicationResource testApplicationResource2 = newApplicationResource().withId(2L).withName("testApplication2Name").build();
        ApplicationResource testApplicationResource3 = newApplicationResource().withId(3L).withName("testApplication3Name").build();

        when(applicationService.findByUserId(testUser1.getId())).thenReturn(Arrays.asList(testApplication1, testApplication2));
        when(applicationService.findByUserId(testUser2.getId())).thenReturn(Arrays.asList(testApplication2, testApplication3));
        when(applicationMapper.mapApplicationToResource(testApplication1)).thenReturn(testApplicationResource1);
        when(applicationMapper.mapApplicationToResource(testApplication2)).thenReturn(testApplicationResource2);
        when(applicationMapper.mapApplicationToResource(testApplication3)).thenReturn(testApplicationResource3);


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
        List<Application> applications = newApplication().build(applicationNumber);
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

        Application application = newApplication().withName(applicationName).build();
        ApplicationResource applicationResource = newApplicationResource().withName(applicationName).build();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode applicationNameNode = mapper.createObjectNode().put("name", applicationName);

        when(applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(competitionId, userId, applicationNameNode)).thenReturn(application);
        when(applicationMapper.mapApplicationToResource(application)).thenReturn(applicationResource);

        mockMvc.perform(post("/application/createApplicationByName/" + competitionId + "/" + userId, "json")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(applicationNameNode)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", notNullValue()))
                .andDo(document("application/create-application"));
    }
}