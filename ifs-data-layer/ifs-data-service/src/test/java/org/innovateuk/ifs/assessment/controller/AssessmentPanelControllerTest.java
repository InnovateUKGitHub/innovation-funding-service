package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentReviewResourceBuilder.newAssessmentReviewResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentPanelControllerTest extends BaseControllerMockMVCTest<AssessmentPanelController> {

    private static final long applicationId = 1L;
    private static final long competitionId = 5L;
    private static final long userId = 2L;

    @Override
    public AssessmentPanelController supplyControllerUnderTest() {
        return new AssessmentPanelController();
    }

    @Test
    public void assignApplication() throws Exception {
        when(assessmentPanelServiceMock.assignApplicationToPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/assignApplication/{applicationId}", applicationId))
                .andExpect(status().isOk());

        verify(assessmentPanelServiceMock, only()).assignApplicationToPanel(applicationId);
    }

    @Test
    public void unAssignApplication() throws Exception {
        when(assessmentPanelServiceMock.unassignApplicationFromPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/unassignApplication/{applicationId}", applicationId))
                .andExpect(status().isOk());

        verify(assessmentPanelServiceMock, only()).unassignApplicationFromPanel(applicationId);
    }

    @Test
    public void notifyAssessors() throws Exception {
        when(assessmentPanelServiceMock.createAndNotifyReviews(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanel/notify-assessors/{competitionId}", competitionId))
                .andExpect(status().isOk());

        verify(assessmentPanelServiceMock, only()).createAndNotifyReviews(competitionId);
    }

    @Test
    public void isPendingReviewNotifications() throws Exception {
        Boolean expected = true;
        when(assessmentPanelServiceMock.isPendingReviewNotifications(competitionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessmentpanel/notify-assessors/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(assessmentPanelServiceMock, only()).isPendingReviewNotifications(competitionId);
    }

    @Test
    public void getAssessmentReviews() throws Exception {
        List<AssessmentReviewResource> assessmentReviews = newAssessmentReviewResource().build(2);

        when(assessmentPanelServiceMock.getAssessmentReviews(userId, competitionId)).thenReturn(serviceSuccess(assessmentReviews));

        mockMvc.perform(get("/assessmentpanel/user/{userId}/competition/{competitionId}", userId, competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(assessmentReviews)));

        verify(assessmentPanelServiceMock, only()).getAssessmentReviews(userId, competitionId);
    }
}