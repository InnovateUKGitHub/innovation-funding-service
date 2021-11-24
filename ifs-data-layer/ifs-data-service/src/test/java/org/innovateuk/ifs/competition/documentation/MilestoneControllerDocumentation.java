package org.innovateuk.ifs.competition.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.controller.MilestoneController;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.resource.MilestoneType;
import org.innovateuk.ifs.competition.transactional.MilestoneService;
import org.innovateuk.ifs.documentation.MilestoneResourceDocs;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.documentation.MilestoneResourceDocs.milestoneResourceFields;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MilestoneControllerDocumentation extends BaseControllerMockMVCTest<MilestoneController> {

    @Mock
    private MilestoneService milestoneService;

    @Override
    protected MilestoneController supplyControllerUnderTest() {
        return new MilestoneController();
    }

    @Test
    public void getAllPublicMilestonesByCompetitionId() throws Exception {
        final Long competitionId = 1L;

        when(milestoneService.getAllPublicMilestonesByCompetitionId(competitionId)).thenReturn(serviceSuccess(newMilestoneResource().build(1)));

        mockMvc.perform(get("/milestone/{id}/public", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllMilestonesByCompetitionId() throws Exception {
        final Long competitionId = 1L;

        when(milestoneService.getAllMilestonesByCompetitionId(competitionId)).thenReturn(serviceSuccess(newMilestoneResource().build(1)));

        mockMvc.perform(get("/milestone/{id}/", competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void getMilestoneByTypeAndCompetitionId() throws Exception {
        Long competitionId = 2L;
        when(milestoneService.getMilestoneByTypeAndCompetitionId(MilestoneType.OPEN_DATE, competitionId)).thenReturn(serviceSuccess(newMilestoneResource().build()));

        mockMvc.perform(get("/milestone/{competitionId}/get-by-type?type=" + MilestoneType.OPEN_DATE, competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());
    }

    @Test
    public void saveMilestones() throws Exception {
        List<MilestoneResource> milestoneResources = newMilestoneResource().build(1);

        when(milestoneService.updateMilestones(anyList()))
                .thenReturn(serviceSuccess());

        mockMvc.perform(put("/milestone/many")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(milestoneResources))
                .header("IFS_AUTH_TOKEN", "123abc"))
            .andExpect(status().isOk());
    }

    @Test
    public void saveMilestone() throws Exception {
        MilestoneResource milestoneResource = newMilestoneResource().build();
        when(milestoneService.updateMilestone(any(MilestoneResource.class)))
                .thenReturn(serviceSuccess());

        mockMvc.perform(put("/milestone/")
                .header("IFS_AUTH_TOKEN", "123abc")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(milestoneResource)))
                .andExpect(status().isOk());
    }

    @Test
    public void updateCompletionStage() throws Exception {

        long competitionId = 123L;

        when(milestoneService.updateCompletionStage(123L, CompetitionCompletionStage.PROJECT_SETUP))
                .thenReturn(serviceSuccess());

        mockMvc.perform(put("/milestone/competition/{competitionId}/completion-stage?completionStage=" +
                    CompetitionCompletionStage.PROJECT_SETUP, competitionId)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk());

        verify(milestoneService).updateCompletionStage(123L, CompetitionCompletionStage.PROJECT_SETUP);
    }
}
