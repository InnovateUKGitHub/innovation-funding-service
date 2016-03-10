package com.worth.ifs.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.application.domain.Application;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.domain.Competition;
import com.worth.ifs.user.domain.User;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static java.util.Arrays.asList;
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

    @Override
    protected ApplicationController supplyControllerUnderTest() {
        return new ApplicationController();
    }

    @Test
    public void applicationControllerShouldReturnApplicationById() throws Exception {
        Application testApplication1 = newApplication().withId(1L).withName("testApplication1Name").build();
        Application testApplication2 = newApplication().withId(2L).withName("testApplication2Name").build();
        Competition competition = newCompetition().withName("Technology Inspired").build();
        ApplicationResource testApplicationResource1 = newApplicationResource().withId(1L).withCompetition(competition).withName("testApplication1Name").build();
        ApplicationResource testApplicationResource2 = newApplicationResource().withId(2L).withCompetition(competition).withName("testApplication2Name").build();

        when(applicationService.getApplicationById(testApplication1.getId())).thenReturn(serviceSuccess(testApplicationResource1));
        when(applicationService.getApplicationById(testApplication2.getId())).thenReturn(serviceSuccess(testApplicationResource2));

        mockMvc.perform(get("/application/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(testApplicationResource1)))
                .andDo(document("application/get-application",
                        responseFields(
                                fieldWithPath("id").description("Id of the application"),
                                fieldWithPath("name").description("Name of the application"),
                                fieldWithPath("startDate").description("Estimated timescales: project start date"),
                                fieldWithPath("submittedDate").description("The date the applicant has submitted this application."),
                                fieldWithPath("durationInMonths").description("Estimated timescales: project duration in months"),
                                fieldWithPath("processRoles").description("list of ProcessRole Id's"),
                                fieldWithPath("applicationStatus").description("ApplicationStatus Id"),
                                fieldWithPath("competition").description("Competition Id"),
                                fieldWithPath("competitionName").description("Competition Name"),
                                fieldWithPath("applicationFinances").description("list of ApplicationFinance Id's"))));
        mockMvc.perform(get("/application/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(testApplicationResource2)));
    }

    @Test
    public void applicationControllerShouldReturnApplicationByUserId() throws Exception {
        User testUser2 = new User(2L, "testUser2", "email2@email.nl", "password", "testToken456def", null, "my-uid");
        User testUser1 = new User(1L, "testUser1", "email1@email.nl", "password", "testToken123abc", null, "my-uid2");

        ApplicationResource testApplicationResource1 = newApplicationResource().withId(1L).withName("testApplication1Name").build();
        ApplicationResource testApplicationResource2 = newApplicationResource().withId(2L).withName("testApplication2Name").build();
        ApplicationResource testApplicationResource3 = newApplicationResource().withId(3L).withName("testApplication3Name").build();

        when(applicationService.findByUserId(testUser1.getId())).thenReturn(serviceSuccess(asList(testApplicationResource1, testApplicationResource2)));
        when(applicationService.findByUserId(testUser2.getId())).thenReturn(serviceSuccess(asList(testApplicationResource2, testApplicationResource3)));

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
        List<ApplicationResource> applications = newApplicationResource().build(applicationNumber);
        when(applicationService.findAll()).thenReturn(serviceSuccess(applications));

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

        ApplicationResource applicationResource = newApplicationResource().withName(applicationName).build();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode applicationNameNode = mapper.createObjectNode().put("name", applicationName);

        when(applicationService.createApplicationByApplicationNameForUserIdAndCompetitionId(competitionId, userId, applicationName)).thenReturn(serviceSuccess(applicationResource));

        mockMvc.perform(post("/application/createApplicationByName/" + competitionId + "/" + userId, "json")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(applicationNameNode)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", notNullValue()))
                .andDo(document("application/create-application"));
    }
}