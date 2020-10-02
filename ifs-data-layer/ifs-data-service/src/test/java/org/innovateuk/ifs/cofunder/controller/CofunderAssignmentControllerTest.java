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

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
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

        mockMvc.perform(delete("/assignment/{userId}/decision", assignmentId)
                .content(toJson(decision)))
                .andExpect(status().isOk());

        verify(cofunderAssignmentService, only()).decision(assignmentId, decision);
    }

    @Test
    public void edit() throws Exception {
        long assignmentId = 1L;
        when(cofunderAssignmentService.edit(assignmentId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/assignment/{userId}/edit", assignmentId))
                .andExpect(status().isOk());

        verify(cofunderAssignmentService, only()).edit(assignmentId);
    }

    @Test
    public void findApplicationsNeedingCofunders() throws Exception {
        long competitionId = 1L;
        String filter = "filter";
        PageRequest pageRequest = PageRequest.of(0, 10);
        ApplicationsForCofundingPageResource expected = new ApplicationsForCofundingPageResource();
        when(cofunderAssignmentService.findApplicationsNeedingCofunders(competitionId, filter, pageRequest)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/competition/{competitionId}?filter={filter}&page={page}&size={size}", competitionId, filter, pageRequest.getPageNumber(), pageRequest.getPageSize()))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(cofunderAssignmentService, only()).findApplicationsNeedingCofunders(competitionId, filter, pageRequest);
    }

    @Test
    public void findAvailableCofundersForApplication() throws Exception {
        long applicationId = 1L;
        String filter = "filter";
        PageRequest pageRequest = PageRequest.of(0, 10);
        CofundersAvailableForApplicationPageResource expected = new CofundersAvailableForApplicationPageResource();
        when(cofunderAssignmentService.findAvailableCofundersForApplication(applicationId, filter, pageRequest)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/application/{applicationId}?filter={filter}&page={page}&size={size}", applicationId, filter, pageRequest.getPageNumber(), pageRequest.getPageSize()))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(expected)));

        verify(cofunderAssignmentService, only()).findAvailableCofundersForApplication(applicationId, filter, pageRequest);
    }
}
