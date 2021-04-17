package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.transactional.ApplicationNotificationService;
import org.innovateuk.ifs.competition.resource.SpendProfileStatusResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.junit.Test;
import org.mockito.Mock;

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

    @Mock
    private CompetitionService competitionService;

    @Mock
    private ApplicationNotificationService applicationNotificationService;

    @Override
    protected CompetitionPostSubmissionController supplyControllerUnderTest() {
        return new CompetitionPostSubmissionController();
    }

    @Test
    public void releaseFeedback() throws Exception {
        final Long competitionId = 1L;

        when(competitionService.releaseFeedback(competitionId)).thenReturn(serviceSuccess());
        when(applicationNotificationService.notifyApplicantsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/post-submission/{id}/release-feedback", competitionId))
                .andExpect(status().isOk());

        verify(competitionService, only()).releaseFeedback(competitionId);
        verify(applicationNotificationService).notifyApplicantsByCompetition(competitionId);
    }

    @Test
    public void closeAssessment() throws Exception {
        final long competitionId = 1L;

        when(competitionService.closeAssessment(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/post-submission/{id}/close-assessment", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(competitionService, only()).closeAssessment(competitionId);
    }

    @Test
    public void reopenAssessment() throws Exception {
        final long competitionId = 1L;

        when(competitionService.reopenAssessmentPeriod(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/post-submission/{id}/reopen-assessment-period", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(competitionService, only()).reopenAssessmentPeriod(competitionId);
    }

    @Test
    public void getPendingSpendProfiles() throws Exception {
        final Long competitionId = 1L;

        List<SpendProfileStatusResource> pendingSpendProfiles = new ArrayList<>();
        when(competitionService.getPendingSpendProfiles(competitionId)).thenReturn(serviceSuccess(pendingSpendProfiles));

        mockMvc.perform(get("/competition/post-submission/{competitionId}/pending-spend-profiles", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingSpendProfiles)));

        verify(competitionService, only()).getPendingSpendProfiles(competitionId);

    }

    @Test
    public void countPendingSpendProfiles() throws Exception {
        final Long competitionId = 1L;
        final Long pendingSpendProfileCount = 3L;

        when(competitionService.countPendingSpendProfiles(competitionId)).thenReturn(serviceSuccess(pendingSpendProfileCount));

        mockMvc.perform(get("/competition/post-submission/{competitionId}/count-pending-spend-profiles", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingSpendProfileCount)));

        verify(competitionService, only()).countPendingSpendProfiles(competitionId);

    }
}
