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
    private CompetitionService competitionServiceMock;

    @Mock
    private ApplicationNotificationService applicationNotificationServiceMock;

    @Override
    protected CompetitionPostSubmissionController supplyControllerUnderTest() {
        return new CompetitionPostSubmissionController();
    }

    @Test
    public void releaseFeedback() throws Exception {
        final Long competitionId = 1L;

        when(competitionServiceMock.releaseFeedback(competitionId)).thenReturn(serviceSuccess());
        when(applicationNotificationServiceMock.notifyApplicantsByCompetition(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/postSubmission/{id}/release-feedback", competitionId))
                .andExpect(status().isOk());

        verify(competitionServiceMock, only()).releaseFeedback(competitionId);
        verify(applicationNotificationServiceMock).notifyApplicantsByCompetition(competitionId);
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
    public void getPendingSpendProfiles() throws Exception {
        final Long competitionId = 1L;

        List<SpendProfileStatusResource> pendingSpendProfiles = new ArrayList<>();
        when(competitionServiceMock.getPendingSpendProfiles(competitionId)).thenReturn(serviceSuccess(pendingSpendProfiles));

        mockMvc.perform(get("/competition/postSubmission/{competitionId}/pending-spend-profiles", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingSpendProfiles)));

        verify(competitionServiceMock, only()).getPendingSpendProfiles(competitionId);

    }

    @Test
    public void countPendingSpendProfiles() throws Exception {
        final Long competitionId = 1L;
        final Long pendingSpendProfileCount = 3L;

        when(competitionServiceMock.countPendingSpendProfiles(competitionId)).thenReturn(serviceSuccess(pendingSpendProfileCount));

        mockMvc.perform(get("/competition/postSubmission/{competitionId}/count-pending-spend-profiles", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(pendingSpendProfileCount)));

        verify(competitionServiceMock, only()).countPendingSpendProfiles(competitionId);

    }
}
