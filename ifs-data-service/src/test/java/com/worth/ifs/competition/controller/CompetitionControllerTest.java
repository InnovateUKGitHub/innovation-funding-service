package com.worth.ifs.competition.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionController> {

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Test
    public void closeAssessment() throws Exception {
        final Long competitionId = 1L;

        when(competitionServiceMock.closeAssessment(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/{id}/close-assessment", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

    }
}
