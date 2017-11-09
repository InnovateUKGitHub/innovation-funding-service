package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationPageResource;
import org.innovateuk.ifs.competition.resource.CompetitionPendingSpendProfilesResource;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionPostSubmissionControllerTest extends BaseControllerMockMVCTest<CompetitionPostSubmissionController> {

    @Override
    protected CompetitionPostSubmissionController supplyControllerUnderTest() {
        return new CompetitionPostSubmissionController();
    }

    @Test
    public void notifyAssessors() throws Exception {
        final Long competitionId = 1L;

        when(competitionServiceMock.notifyAssessors(competitionId)).thenReturn(serviceSuccess());
        when(assessorServiceMock.notifyAssessorsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/postSubmission/{id}/notify-assessors", competitionId))
                .andExpect(status().isOk());

        verify(competitionServiceMock, only()).notifyAssessors(competitionId);
        verify(assessorServiceMock).notifyAssessorsByCompetition(competitionId);
    }

    @Test
    public void releaseFeedback() throws Exception {
        final Long competitionId = 1L;

        when(competitionServiceMock.releaseFeedback(competitionId)).thenReturn(serviceSuccess());
        when(applicationServiceMock.notifyApplicantsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/postSubmission/{id}/release-feedback", competitionId))
                .andExpect(status().isOk());

        verify(competitionServiceMock, only()).releaseFeedback(competitionId);
        verify(applicationServiceMock).notifyApplicantsByCompetition(competitionId);
    }

    @Test
    public void closeAssessment() throws Exception {
        final Long competitionId = 1L;

        when(competitionServiceMock.closeAssessment(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/postSubmission/{id}/close-assessment", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(competitionServiceMock, only()).closeAssessment(competitionId);
    }

    @Test
    public void findUnsuccessfulApplications() throws Exception {
        final Long competitionId = 1L;
        int pageIndex = 0;
        int pageSize = 20;
        String sortField = "id";

        ApplicationPageResource applicationPage = new ApplicationPageResource();

        when(competitionServiceMock.findUnsuccessfulApplications(competitionId, pageIndex, pageSize, sortField)).thenReturn(serviceSuccess(applicationPage));

        mockMvc.perform(get("/competition/postSubmission/{id}/unsuccessful-applications?page={page}&size={pageSize}&sort={sortField}", competitionId, pageIndex, pageSize, sortField))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(applicationPage)));

        verify(competitionServiceMock, only()).findUnsuccessfulApplications(competitionId, pageIndex, pageSize, sortField);
    }

    @Test
    public void getPendingSpendProfiles() throws Exception {
        final Long competitionId = 1L;

        List<CompetitionPendingSpendProfilesResource> pendingSpendProfiles = new ArrayList<>();
        when(competitionServiceMock.getPendingSpendProfiles(competitionId)).thenReturn(serviceSuccess(pendingSpendProfiles));

        mockMvc.perform(get("/competition/postSubmission/{competitionId}/pending-spend-profiles", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingSpendProfiles)));

        verify(competitionServiceMock, only()).getPendingSpendProfiles(competitionId);

    }

    @Test
    public void countPendingSpendProfiles() throws Exception {
        final Long competitionId = 1L;
        final Integer pendingSpendProfileCount = 3;

        when(competitionServiceMock.countPendingSpendProfiles(competitionId)).thenReturn(serviceSuccess(pendingSpendProfileCount));

        mockMvc.perform(get("/competition/postSubmission/{competitionId}/count-pending-spend-profiles", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingSpendProfileCount)));

        verify(competitionServiceMock, only()).countPendingSpendProfiles(competitionId);

    }
}
