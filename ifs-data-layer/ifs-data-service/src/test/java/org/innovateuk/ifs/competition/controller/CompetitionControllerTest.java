package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionController> {

    @Mock
    private CompetitionService competitionService;

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Test
    public void updateTermsAndConditionsForCompetition() throws Exception {
        final long competitionId = 1L;
        final long termsAndConditionsId = 2L;

        when(competitionService.updateTermsAndConditionsForCompetition(competitionId, termsAndConditionsId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/{id}/update-terms-and-conditions/{tcId}", competitionId, termsAndConditionsId))
                .andExpect(status().isOk());

        verify(competitionService, only()).updateTermsAndConditionsForCompetition(competitionId, termsAndConditionsId);
    }

    @Test
    public void downloadTerms() throws Exception {
        final long competitionId = 1L;
        String fileName = "filename";
        String fileContent = "content";
        FileAndContents fileAndContents =  new BasicFileAndContents(newFileEntryResource().build(), () -> {
            try {
                return new MockMultipartFile(fileName, fileContent.getBytes()).getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });

        when(competitionService.downloadTerms(competitionId)).thenReturn(serviceSuccess(fileAndContents));

        mockMvc.perform(get("/competition/{id}/terms-and-conditions", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(fileContent));

        verify(competitionService, only()).downloadTerms(competitionId);
    }
}
