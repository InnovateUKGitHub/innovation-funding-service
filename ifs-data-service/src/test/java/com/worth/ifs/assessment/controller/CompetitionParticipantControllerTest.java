package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.transactional.CompetitionParticipantService;
import com.worth.ifs.invite.resource.CompetitionParticipantResource;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.List;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
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

        when(competitionParticipantService.getCompetitionParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED)).thenReturn(serviceSuccess(competitionParticipants));

        mockMvc.perform(get("/competitionparticipant/user/{userId}/role/{role}/status/{status}", 1L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(toJson(competitionParticipants)))
                .andExpect(status().isOk());

        verify(competitionParticipantService, times(1)).getCompetitionParticipants(1L, CompetitionParticipantRoleResource.ASSESSOR, ParticipantStatusResource.ACCEPTED);
    }
}
