package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.util.Lists.emptyList;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionController> {

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Test
    public void notifyAssessors() throws Exception {
        final Long competitionId = 1L;
        final List<AssessmentResource> assessments = newAssessmentResource().build(2);

        when(competitionServiceMock.notifyAssessors(competitionId)).thenReturn(serviceSuccess());
        when(assessmentServiceMock.findByStateAndCompetition(AssessmentStates.CREATED, competitionId)).thenReturn(serviceSuccess(assessments));

        mockMvc.perform(put("/competition/{id}/notify-assessors", competitionId))
                .andExpect(status().isOk());

        verify(competitionServiceMock, only()).notifyAssessors(competitionId);
        verify(assessmentServiceMock).findByStateAndCompetition(AssessmentStates.CREATED, competitionId);
        verify(assessmentServiceMock).notify(assessments.get(0).getId());
        verify(assessmentServiceMock).notify(assessments.get(1).getId());
    }

    @Test
    public void closeAssessment() throws Exception {
        final Long competitionId = 1L;

        when(competitionServiceMock.closeAssessment(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/{id}/close-assessment", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(competitionServiceMock, only()).closeAssessment(competitionId);

    }
}
