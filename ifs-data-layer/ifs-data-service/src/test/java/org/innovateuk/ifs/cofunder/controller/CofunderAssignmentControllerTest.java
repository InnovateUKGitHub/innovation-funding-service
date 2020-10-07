package org.innovateuk.ifs.cofunder.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderAssignmentResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDecisionResource;
import org.innovateuk.ifs.cofunder.resource.CofundersAvailableForApplicationPageResource;
import org.innovateuk.ifs.cofunder.transactional.CofunderAssignmentService;
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

public class CofunderAssignmentControllerTest extends BaseControllerMockMVCTest<CofunderAssignmentController> {
    @Mock
    private CofunderAssignmentService cofunderAssignmentService;

    @Override
    protected CofunderAssignmentController supplyControllerUnderTest() {
        return new CofunderAssignmentController();
    }

    @Test
    public void getAssignment() throws Exception {
        long userId = 1L;
        long applicationId = 2L;
        CofunderAssignmentResource expected = new CofunderAssignmentResource();

        when(cofunderAssignmentService.getAssignment(userId, applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/cofunder/user/{userId}/application/{applicationId}", userId, applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(cofunderAssignmentService, only()).getAssignment(userId, applicationId);
    }

    @Test
    public void assign() throws Exception {
        long userId = 1L;
        long applicationId = 2L;
        CofunderAssignmentResource expected = new CofunderAssignmentResource();

        when(cofunderAssignmentService.assign(userId, applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(post("/cofunder/user/{userId}/application/{applicationId}", userId, applicationId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(cofunderAssignmentService, only()).assign(userId, applicationId);
    }

    @Test
    public void removeAssignment() throws Exception {
        long userId = 1L;
        long applicationId = 2L;

        when(cofunderAssignmentService.removeAssignment(userId, applicationId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/cofunder/user/{userId}/application/{applicationId}", userId, applicationId))
                .andExpect(status().isOk());

        verify(cofunderAssignmentService, only()).removeAssignment(userId, applicationId);
    }

    @Test
    public void decision() throws Exception {
        long assignmentId = 1L;
        CofunderDecisionResource decision = new CofunderDecisionResource();
        when(cofunderAssignmentService.decision(assignmentId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/cofunder/assignment/{assignmentId}/decision", assignmentId)
                .content(toJson(decision))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cofunderAssignmentService, only()).decision(assignmentId, decision);
    }

    @Test
    public void edit() throws Exception {
        long assignmentId = 1L;
        when(cofunderAssignmentService.edit(assignmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/cofunder/assignment/{assignmentId}/edit", assignmentId))
                .andExpect(status().isOk());

        verify(cofunderAssignmentService, only()).edit(assignmentId);
    }

    @Test
    public void findApplicationsNeedingCofunders() throws Exception {
        long competitionId = 1L;
        String filter = "filter";
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "id");
        ApplicationsForCofundingPageResource expected = new ApplicationsForCofundingPageResource();
        when(cofunderAssignmentService.findApplicationsNeedingCofunders(competitionId, filter, pageRequest)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/cofunder/competition/{competitionId}?filter={filter}&page={page}&size={size}", competitionId, filter, page, size))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(cofunderAssignmentService, only()).findApplicationsNeedingCofunders(competitionId, filter, pageRequest);
    }

    @Test
    public void findAvailableCofundersForApplication() throws Exception {
        long applicationId = 1L;
        String filter = "filter";
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "id");
        CofundersAvailableForApplicationPageResource expected = new CofundersAvailableForApplicationPageResource();
        when(cofunderAssignmentService.findAvailableCofundersForApplication(applicationId, filter, pageRequest)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/cofunder/application/{applicationId}?filter={filter}&page={page}&size={size}", applicationId, filter, page, size))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(cofunderAssignmentService, only()).findAvailableCofundersForApplication(applicationId, filter, pageRequest);
    }

    @Test
    public void findAvailableCofundersUserIdsForApplication() throws Exception {
        long applicationId = 1L;
        String filter = "filter";
        List<Long> expected = Arrays.asList(1L, 2L, 3L);
        when(cofunderAssignmentService.findAvailableCofundersUserIdsForApplication(applicationId, filter)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/cofunder/application/{applicationId}/userIds?filter={filter}", applicationId, filter))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(cofunderAssignmentService, only()).findAvailableCofundersUserIdsForApplication(applicationId, filter);
    }
}
