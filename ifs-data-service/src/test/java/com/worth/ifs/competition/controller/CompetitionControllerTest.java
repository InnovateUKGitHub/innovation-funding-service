package com.worth.ifs.competition.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionController> {

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Test
    public void notifyAssessors() throws Exception {
        final Long competitionId = 1L;

        when(competitionServiceMock.notifyAssessors(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/{id}/notify-assessors", competitionId))
                .andExpect(status().isOk());

        verify(competitionServiceMock, only()).notifyAssessors(competitionId);
    }
}
