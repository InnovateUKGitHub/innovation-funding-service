package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.file.service.BasicFileAndContents;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionControllerTest extends BaseControllerMockMVCTest<CompetitionController> {

    @Mock
    private CompetitionService competitionServiceMock;

    @Override
    protected CompetitionController supplyControllerUnderTest() {
        return new CompetitionController();
    }

    @Test
    public void findInnovationLeads() throws Exception {
        final long competitionId = 1L;

        List<UserResource> innovationLeads = new ArrayList<>();
        when(competitionServiceMock.findInnovationLeads(competitionId)).thenReturn(serviceSuccess(innovationLeads));

        mockMvc.perform(get("/competition/{id}/innovation-leads", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(innovationLeads)));

        verify(competitionServiceMock, only()).findInnovationLeads(competitionId);
    }

    @Test
    public void addInnovationLead() throws Exception {
        final long competitionId = 1L;
        final long innovationLeadUserId = 2L;

        when(competitionServiceMock.addInnovationLead(competitionId, innovationLeadUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{id}/add-innovation-lead/{innovationLeadUserId}", competitionId, innovationLeadUserId))
                .andExpect(status().isOk());

        verify(competitionServiceMock, only()).addInnovationLead(competitionId, innovationLeadUserId);
    }

    @Test
    public void removeInnovationLead() throws Exception {
        final long competitionId = 1L;
        final long innovationLeadUserId = 2L;

        when(competitionServiceMock.removeInnovationLead(competitionId, innovationLeadUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{id}/remove-innovation-lead/{innovationLeadUserId}", competitionId, innovationLeadUserId))
                .andExpect(status().isOk());

        verify(competitionServiceMock, only()).removeInnovationLead(competitionId, innovationLeadUserId);
    }

    @Test
    public void testUpdateTermsAndConditionsForCompetition() throws Exception {
        final long competitionId = 1L;
        final long termsAndConditionsId = 2L;

        when(competitionServiceMock.updateTermsAndConditionsForCompetition(competitionId, termsAndConditionsId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/competition/{id}/update-terms-and-conditions/{tcId}", competitionId, termsAndConditionsId))
                .andExpect(status().isOk());

        verify(competitionServiceMock, only()).updateTermsAndConditionsForCompetition(competitionId, termsAndConditionsId);
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

        when(competitionServiceMock.downloadTerms(competitionId)).thenReturn(serviceSuccess(fileAndContents));

        mockMvc.perform(get("/competition/{id}/terms-and-conditions", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(fileContent));

        verify(competitionServiceMock, only()).downloadTerms(competitionId);
    }
}
