package org.innovateuk.ifs.supporter.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.supporter.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.supporter.resource.SupporterAssignmentResource;
import org.innovateuk.ifs.supporter.resource.SupporterDecisionResource;
import org.innovateuk.ifs.supporter.resource.SupportersAvailableForApplicationPageResource;
import org.innovateuk.ifs.supporter.transactional.SupporterAssignmentService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SupporterAssignmentControllerTest extends BaseControllerMockMVCTest<SupporterAssignmentController> {
    @Mock
    private SupporterAssignmentService supporterAssignmentService;

    @Override
    protected SupporterAssignmentController supplyControllerUnderTest() {
        return new SupporterAssignmentController();
    }

    @Test
    public void getAssignment() throws Exception {
        long userId = 1L;
        long applicationId = 2L;
        SupporterAssignmentResource expected = new SupporterAssignmentResource();

        when(supporterAssignmentService.getAssignment(userId, applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/supporter/assignment/user/{userId}/application/{applicationId}", userId, applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(supporterAssignmentService, only()).getAssignment(userId, applicationId);
    }

    @Test
    public void assign() throws Exception {
        long userId = 1L;
        long applicationId = 2L;
        SupporterAssignmentResource expected = new SupporterAssignmentResource();

        when(supporterAssignmentService.assign(userId, applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(post("/supporter/user/{userId}/application/{applicationId}", userId, applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(supporterAssignmentService, only()).assign(userId, applicationId);
    }

    @Test
    public void removeAssignment() throws Exception {
        long userId = 1L;
        long applicationId = 2L;

        when(supporterAssignmentService.removeAssignment(userId, applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/supporter/user/{userId}/application/{applicationId}", userId, applicationId))
                .andExpect(status().isOk());

        verify(supporterAssignmentService, only()).removeAssignment(userId, applicationId);
    }

    @Test
    public void decision() throws Exception {
        long assignmentId = 1L;
        SupporterDecisionResource decision = new SupporterDecisionResource();
        when(supporterAssignmentService.decision(assignmentId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/supporter/assignment/{assignmentId}/decision", assignmentId)
                .content(toJson(decision))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(supporterAssignmentService, only()).decision(assignmentId, decision);
    }

    @Test
    public void edit() throws Exception {
        long assignmentId = 1L;
        when(supporterAssignmentService.edit(assignmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/supporter/assignment/{assignmentId}/edit", assignmentId))
                .andExpect(status().isOk());

        verify(supporterAssignmentService, only()).edit(assignmentId);
    }

    @Test
    public void findApplicationsNeedingSupporters() throws Exception {
        long competitionId = 1L;
        String filter = "filter";
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "id");
        ApplicationsForCofundingPageResource expected = new ApplicationsForCofundingPageResource();
        when(supporterAssignmentService.findApplicationsNeedingSupporters(competitionId, filter, pageRequest)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/supporter/competition/{competitionId}?filter={filter}&page={page}&size={size}", competitionId, filter, page, size))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(supporterAssignmentService, only()).findApplicationsNeedingSupporters(competitionId, filter, pageRequest);
    }

    @Test
    public void findAvailableSupportersForApplication() throws Exception {
        long applicationId = 1L;
        String filter = "filter";
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "id");
        SupportersAvailableForApplicationPageResource expected = new SupportersAvailableForApplicationPageResource();
        when(supporterAssignmentService.findAvailableSupportersForApplication(applicationId, filter, pageRequest)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/supporter/application/{applicationId}?filter={filter}&page={page}&size={size}", applicationId, filter, page, size))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(supporterAssignmentService, only()).findAvailableSupportersForApplication(applicationId, filter, pageRequest);
    }

    @Test
    public void findAvailableSupportersUserIdsForApplication() throws Exception {
        long applicationId = 1L;
        String filter = "filter";
        List<Long> expected = Arrays.asList(1L, 2L, 3L);
        when(supporterAssignmentService.findAvailableSupportersUserIdsForApplication(applicationId, filter)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/supporter/application/{applicationId}/userIds?filter={filter}", applicationId, filter))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(supporterAssignmentService, only()).findAvailableSupportersUserIdsForApplication(applicationId, filter);
    }
}
