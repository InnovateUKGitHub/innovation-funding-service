package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.transactional.CompetitionParticipantService;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantResource;
import org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionParticipantControllerTest extends BaseControllerMockMVCTest<CompetitionParticipantController> {

    @Mock
    private CompetitionParticipantService competitionParticipantService;

    @Override
    protected CompetitionParticipantController supplyControllerUnderTest() {
        return new CompetitionParticipantController();
    }

    @Test
    public void getParticipants() throws Exception {
        List<CompetitionParticipantResource> competitionParticipants = newCompetitionParticipantResource()
                .build(2);

        when(competitionParticipantService.getCompetitionParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR)).thenReturn(serviceSuccess(competitionParticipants));

        mockMvc.perform(get("/competitionparticipant/user/{userId}/role/{role}", 1L, CompetitionParticipantRoleResource.ASSESSOR).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(toJson(competitionParticipants)))
                .andExpect(status().isOk());

        verify(competitionParticipantService, times(1)).getCompetitionParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR);
    }
}
