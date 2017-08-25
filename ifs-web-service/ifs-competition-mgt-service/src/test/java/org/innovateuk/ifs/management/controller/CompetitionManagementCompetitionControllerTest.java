package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Class for testing public functions of {@link CompetitionManagementCompetitionController}
 */
@RunWith(MockitoJUnitRunner.class)
public class CompetitionManagementCompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionManagementCompetitionController> {

    @Override
    protected CompetitionManagementCompetitionController supplyControllerUnderTest() {
        return new CompetitionManagementCompetitionController();
    }

    @Test
    public void closeAssessment() throws Exception {
        long competitionId = 1L;

        when(competitionService.closeAssessment(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/close-assessment", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s", competitionId)));

        verify(competitionService).closeAssessment(competitionId);
        verifyNoMoreInteractions(competitionService);
    }

    @Test
    public void notifyAssessors() throws Exception {
        long competitionId = 1L;

        when(competitionService.notifyAssessors(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{competitionId}/notify-assessors", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s", competitionId)));

        verify(competitionService).notifyAssessors(competitionId);
        verifyNoMoreInteractions(competitionService);
    }

    @Test
    public void releaseFeedback() throws Exception {
        long competitionId = 1L;

      //  when(competitionService.releaseFeedback(competitionId)).thenReturn(Void);

        mockMvc.perform(post("/competition/{competitionId}/release-Feedback", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("dashboard/project-setup"));

        verify(competitionService).releaseFeedback(competitionId);
        verifyNoMoreInteractions(competitionService);
    }
}
