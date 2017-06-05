package org.innovateuk.ifs.application.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.IneligibleOutcome;
import org.innovateuk.ifs.application.resource.ApplicationIneligibleSendResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.IneligibleOutcomeResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationIneligibleSendResourceBuilder.newApplicationIneligibleSendResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeBuilder.newIneligibleOutcome;
import static org.innovateuk.ifs.application.builder.IneligibleOutcomeResourceBuilder.newIneligibleOutcomeResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ApplicationControllerTest extends BaseControllerMockMVCTest<ApplicationController> {

    @Override
    protected ApplicationController supplyControllerUnderTest() {
        return new ApplicationController();
    }

    @Test
    public void applicationControllerShouldReturnApplicationById() throws Exception {
        Long application1Id = 1L;
        Application testApplication1 = newApplication().withId(application1Id).withName("testApplication1Name").build();
        Application testApplication2 = newApplication().withId(2L).withName("testApplication2Name").build();
        Competition competition = newCompetition().withName("Technology Inspired").build();
        ApplicationResource testApplicationResource1 = newApplicationResource().withId(application1Id).withCompetition(competition.getId()).withName("testApplication1Name").build();
        ApplicationResource testApplicationResource2 = newApplicationResource().withId(2L).withCompetition(competition.getId()).withName("testApplication2Name").build();

        when(applicationServiceMock.getApplicationById(testApplication1.getId())).thenReturn(serviceSuccess(testApplicationResource1));
        when(applicationServiceMock.getApplicationById(testApplication2.getId())).thenReturn(serviceSuccess(testApplicationResource2));

        mockMvc.perform(get("/application/{id}", application1Id))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(testApplicationResource1)));

        mockMvc.perform(get("/application/2"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(testApplicationResource2)));
    }

    @Test
    public void applicationControllerShouldReturnApplicationByUserId() throws Exception {
        Long userId = 1L;
        User testUser2 = new User(2L, "test", "User2", "email2@email.nl", "testToken456def", "my-uid");
        User testUser1 = new User(userId, "test", "User1", "email1@email.nl", "testToken123abc", "my-uid2");

        ApplicationResource testApplicationResource1 = newApplicationResource().withId(1L).withName("testApplication1Name").build();
        ApplicationResource testApplicationResource2 = newApplicationResource().withId(2L).withName("testApplication2Name").build();
        ApplicationResource testApplicationResource3 = newApplicationResource().withId(3L).withName("testApplication3Name").build();

        when(applicationServiceMock.findByUserId(testUser1.getId())).thenReturn(serviceSuccess(asList(testApplicationResource1, testApplicationResource2)));
        when(applicationServiceMock.findByUserId(testUser2.getId())).thenReturn(serviceSuccess(asList(testApplicationResource2, testApplicationResource3)));

        mockMvc.perform(get("/application/findByUser/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0]name", is("testApplication1Name")))
                .andExpect(jsonPath("[0]id", is(1)))
                .andExpect(jsonPath("[1]name", is("testApplication2Name")))
                .andExpect(jsonPath("[1]id", is(2)));

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
        when(applicationServiceMock.findAll()).thenReturn(serviceSuccess(applications));

        mockMvc.perform(get("/application/").contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(applicationNumber)));
    }

    @Test
    public void applicationControllerCanCreateApplication() throws Exception {
        Long competitionId = 1L;
        Long userId = 1L;
        String applicationName = "testApplication";

        ApplicationResource applicationResource = newApplicationResource().withName(applicationName).build();

        ObjectNode applicationNameNode = objectMapper.createObjectNode().put("name", applicationName);

        when(applicationServiceMock.createApplicationByApplicationNameForUserIdAndCompetitionId(applicationName, competitionId, userId)).thenReturn(serviceSuccess(applicationResource));

        mockMvc.perform(post("/application/createApplicationByName/{competitionId}/{userId}", competitionId, userId, "json")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applicationNameNode)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", notNullValue()));
    }

    @Test
    public void applicationControllerApplicationReadyToSubmit() throws Exception {

        Application app = newApplication().build();

        when(applicationServiceMock.applicationReadyForSubmit(app.getId())).thenReturn(serviceSuccess(Boolean.TRUE));

        mockMvc.perform(get("/application/applicationReadyForSubmit/{applicationId}", app.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Boolean.TRUE)));
    }

    @Test
    public void markAsIneligible() throws Exception {
        Long applicationId = 1L;

        IneligibleOutcomeResource ineligibleOutcomeResource = newIneligibleOutcomeResource()
                .withReason("Reason")
                .build();

        IneligibleOutcome reason = newIneligibleOutcome()
                .withReason("Reason")
                .build();

        when(ineligibleOutcomeMapperMock.mapToDomain(ineligibleOutcomeResource)).thenReturn(reason);
        when(applicationServiceMock.markAsIneligible(applicationId, reason)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/application/{applicationId}/ineligible", applicationId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ineligibleOutcomeResource)))
                .andExpect(status().isOk());

        verify(ineligibleOutcomeMapperMock).mapToDomain(ineligibleOutcomeResource);
        verify(applicationServiceMock).markAsIneligible(applicationId, reason);
    }

    @Test
    public void informIneligible() throws Exception {
        long applicationId = 1L;
        ApplicationIneligibleSendResource resource = newApplicationIneligibleSendResource().build();
        when(applicationServiceMock.informIneligible(applicationId, resource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/application/informIneligible/{applicationId}", applicationId)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk());
    }

    @Test
    public void showApplicationTeam() throws Exception {
        long applicationId = 1L;
        long userId = 2L;
        when(applicationServiceMock.showApplicationTeam(applicationId, userId)).thenReturn(serviceSuccess(Boolean.TRUE));

        mockMvc.perform(get("/application/showApplicationTeam/{applicationId}/{userId}", applicationId, userId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Boolean.TRUE)));
    }
}
