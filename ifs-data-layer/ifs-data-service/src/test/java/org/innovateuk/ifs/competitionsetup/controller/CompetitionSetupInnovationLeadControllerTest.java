package org.innovateuk.ifs.competitionsetup.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.transactional.CompetitionSetupInnovationLeadService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionSetupInnovationLeadControllerTest extends BaseControllerMockMVCTest<CompetitionSetupInnovationLeadController> {

    @Mock
    private CompetitionSetupInnovationLeadService competitionSetupInnovationLeadService;

    @Override
    protected CompetitionSetupInnovationLeadController supplyControllerUnderTest() {
        return new CompetitionSetupInnovationLeadController();
    }

    @Test
    public void findAvailableInnovationLeadsNotAssignedToCompetition() throws Exception {
        final long competitionId = 1L;

        List<UserResource> innovationLeads = new ArrayList<>();
        when(competitionSetupInnovationLeadService.findInnovationLeads(competitionId)).thenReturn(serviceSuccess(innovationLeads));

        mockMvc.perform(get("/competition/{id}/innovation-leads", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(innovationLeads)));

        verify(competitionSetupInnovationLeadService, only()).findInnovationLeads(competitionId);
    }

    @Test
    public void findInnovationLeadsAddedToCompetition() throws Exception {
        final long competitionId = 1L;

        List<UserResource> innovationLeads = new ArrayList<>();
        when(competitionSetupInnovationLeadService.findInnovationLeads(competitionId)).thenReturn(serviceSuccess(innovationLeads));

        mockMvc.perform(get("/competition/{id}/innovation-leads/find-added", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(innovationLeads)));

        verify(competitionSetupInnovationLeadService, only()).findInnovationLeads(competitionId);
    }

    @Test
    public void addInnovationLead() throws Exception {
        final long competitionId = 1L;
        final long innovationLeadUserId = 2L;

        when(competitionSetupInnovationLeadService.addInnovationLead(competitionId, innovationLeadUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{id}/add-innovation-lead/{innovationLeadUserId}", competitionId, innovationLeadUserId))
                .andExpect(status().isOk());

        verify(competitionSetupInnovationLeadService, only()).addInnovationLead(competitionId, innovationLeadUserId);
    }

    @Test
    public void removeInnovationLead() throws Exception {
        final long competitionId = 1L;
        final long innovationLeadUserId = 2L;

        when(competitionSetupInnovationLeadService.removeInnovationLead(competitionId, innovationLeadUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/{id}/remove-innovation-lead/{innovationLeadUserId}", competitionId, innovationLeadUserId))
                .andExpect(status().isOk());

        verify(competitionSetupInnovationLeadService, only()).removeInnovationLead(competitionId, innovationLeadUserId);
    }
}